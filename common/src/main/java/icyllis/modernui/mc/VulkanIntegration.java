package icyllis.modernui.mc;

import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanCommandEncoder;
import com.mojang.renderpearl.backend.vulkan.VulkanConst;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuTexture;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuTextureView;
import com.mojang.renderpearl.frontend.FrontendGpuDevice;
import icyllis.arc3d.vulkan.VKUtil;
import icyllis.arc3d.vulkan.VulkanBackendContext;
import icyllis.arc3d.vulkan.VulkanImage;
import icyllis.arc3d.vulkan.VulkanMemoryAllocator;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkImageCopy;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;

import static org.lwjgl.vulkan.VK10.*;

public final class VulkanIntegration {

    private VulkanIntegration() {
    }

    private static VulkanDevice backend(GpuDevice device) {
        return (VulkanDevice) ((FrontendGpuDevice) device).backend;
    }

    public static VulkanBackendContext wrapContext(GpuDevice device, VkPhysicalDeviceFeatures2 features) {
        VulkanDevice backend = backend(device);
        VulkanBackendContext context = new VulkanBackendContext();
        context.mDevice = backend.vkDevice();
        context.mInstance = backend.instance().vkInstance();
        context.mPhysicalDevice = context.mDevice.getPhysicalDevice();
        context.mQueue = backend.graphicsQueue().vkQueue();
        context.mGraphicsQueueIndex = backend.graphicsQueue().queueFamilyIndex();
        context.mMaxAPIVersion = context.mDevice.getCapabilities().apiVersion;
        context.mDeviceFeatures2 = features;
        context.mMemoryAllocator = new VulkanMemoryAllocator(backend.vma(), true);
        return context;
    }

    public static void useRedAsAlpha(GpuDevice device, VulkanGpuTextureView view) {
        var vkDevice = backend(device).vkDevice();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var info = VkImageViewCreateInfo.calloc(stack).sType$Default()
                    .image(view.texture().vkImage())
                    .viewType(VK_IMAGE_VIEW_TYPE_2D)
                    .format(VulkanConst.toVk(view.texture().getFormat()));
            info.components().set(VK_COMPONENT_SWIZZLE_ONE, VK_COMPONENT_SWIZZLE_ONE,
                    VK_COMPONENT_SWIZZLE_ONE, VK_COMPONENT_SWIZZLE_R);
            info.subresourceRange().set(VK_IMAGE_ASPECT_COLOR_BIT, view.baseMipLevel(), view.mipLevels(), 0, 1);
            var imageView = stack.mallocLong(1);
            VKUtil._CHECK_(vkCreateImageView(vkDevice, info, null, imageView));
            vkDestroyImageView(vkDevice, view.vkImageView(), null);
            view.vkImageView = imageView.get(0);
        }
    }

    static void copyLayer(GpuDevice device, VulkanImage source, VulkanGpuTexture destination) {
        VulkanCommandEncoder encoder = backend(device).createCommandEncoder();
        VkCommandBuffer commands = encoder.commandBuffer();
        int layout = source.getVulkanMutableState().getImageLayout();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var barrier = VkImageMemoryBarrier.calloc(1, stack).sType$Default()
                    .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .image(source.vkImage());
            barrier.subresourceRange().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1);
            barrier.oldLayout(layout).newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                    .srcAccessMask(VK_ACCESS_MEMORY_WRITE_BIT).dstAccessMask(VK_ACCESS_TRANSFER_READ_BIT);
            vkCmdPipelineBarrier(commands, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT,
                    0, null, null, barrier);

            var region = VkImageCopy.calloc(1, stack);
            region.srcSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1);
            region.dstSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1);
            region.extent().set(source.getWidth(), source.getHeight(), 1);
            // RenderPearl keeps its own textures in GENERAL.
            vkCmdCopyImage(commands, source.vkImage(), VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                    destination.vkImage(), VK_IMAGE_LAYOUT_GENERAL, region);

            barrier.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL).newLayout(layout)
                    .srcAccessMask(VK_ACCESS_TRANSFER_READ_BIT)
                    .dstAccessMask(VK_ACCESS_MEMORY_READ_BIT | VK_ACCESS_MEMORY_WRITE_BIT);
            vkCmdPipelineBarrier(commands, VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT,
                    0, null, null, barrier);
            VulkanCommandEncoder.memoryBarrier(commands, stack);
        }
        source.refCommandBuffer();
        encoder.queueForDestroy(source::unrefCommandBuffer);
    }
}
