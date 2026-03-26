<template>
  <div class="image-upload-field">
    <el-upload
      v-if="!disabled"
      v-model:file-list="fileList"
      list-type="picture-card"
      :http-request="handleUpload"
      :limit="limit"
      :multiple="multiple"
      :accept="accept"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>

    <div v-else class="preview-list">
      <el-image
        v-for="(url, index) in urls"
        :key="`${url}-${index}`"
        :src="url"
        :preview-src-list="urls"
        fit="cover"
        class="preview-image"
      />
      <span v-if="!urls.length" class="empty-text">暂无图片</span>
    </div>

    <div v-if="!disabled" class="tips">
      支持上传图片，上传成功后会自动保存为图片地址。
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { fileApi } from '../api'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  multiple: {
    type: Boolean,
    default: false
  },
  limit: {
    type: Number,
    default: 1
  },
  accept: {
    type: String,
    default: 'image/*'
  }
})

const emit = defineEmits(['update:modelValue'])

const fileList = ref([])

const urls = computed(() => splitUrls(props.modelValue))

function splitUrls(value) {
  return String(value || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

function toFileList(urlList) {
  return urlList.map((url, index) => ({
    name: `image-${index + 1}`,
    url,
    status: 'success'
  }))
}

watch(
  () => props.modelValue,
  (value) => {
    fileList.value = toFileList(splitUrls(value))
  },
  { immediate: true }
)

function emitUrls(urlList) {
  emit('update:modelValue', urlList.join(','))
}

async function handleUpload(option) {
  try {
    const res = await fileApi.upload(option.file)
    const uploadedUrl = res?.data || ''
    if (!uploadedUrl) {
      throw new Error('上传返回为空')
    }

    const nextUrls = props.multiple ? [...urls.value, uploadedUrl] : [uploadedUrl]
    emitUrls(nextUrls)
    option.onSuccess?.(res)
  } catch (error) {
    ElMessage.error('图片上传失败')
    option.onError?.(error)
  }
}

function handleRemove(file) {
  const nextUrls = urls.value.filter((url) => url !== file.url)
  emitUrls(nextUrls)
}

function handleExceed() {
  ElMessage.warning(`最多上传 ${props.limit} 张图片`)
}
</script>

<style scoped>
.preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.preview-image {
  width: 104px;
  height: 104px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
}

.empty-text {
  color: #909399;
  font-size: 13px;
}

.tips {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
