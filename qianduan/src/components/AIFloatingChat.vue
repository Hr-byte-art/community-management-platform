<template>
  <div
    class="ai-float-button"
    :style="buttonStyle"
    @mousedown="handlePointerDown"
    @click="handleButtonClick"
  >
    <div class="bot-shell">
      <span class="bot-antenna"></span>
      <div class="bot-face">
        <span></span>
        <span></span>
      </div>
      <div class="bot-mouth"></div>
    </div>
  </div>

  <el-drawer
    v-model="visible"
    :with-header="false"
    :size="420"
    append-to-body
    class="ai-chat-drawer"
  >
    <div class="ai-chat-panel">
      <div class="ai-chat-header">
        <div>
          <div class="ai-chat-title">社区智能助手</div>
          <div class="ai-chat-subtitle">欢迎咨询社区服务、工单、预约等问题</div>
        </div>
        <div class="ai-chat-header-actions">
          <el-button link @click="resetMessages">清空</el-button>
          <el-button link @click="visible = false">关闭</el-button>
        </div>
      </div>

      <div ref="messageListRef" class="ai-chat-messages">
        <div
          v-for="item in messages"
          :key="item.id"
          class="chat-row"
          :class="item.role"
        >
          <div class="chat-bubble">
            <div class="chat-role">{{ item.role === 'user' ? '我' : 'AI 助手' }}</div>
            <div class="chat-content">{{ item.content }}</div>
            <div v-if="item.streaming" class="chat-streaming">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="ai-chat-input">
        <el-input
          v-model="question"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 5 }"
          resize="none"
          maxlength="1000"
          show-word-limit
          placeholder="输入你想咨询的问题，按 Enter 发送，Shift + Enter 换行"
          @keydown="handleInputKeydown"
        />
        <div class="ai-chat-footer">
          <span class="ai-chat-tip">{{ loading ? 'AI 正在回复中...' : '支持流式输出' }}</span>
          <el-button type="primary" :loading="loading" @click="handleSend">发送</el-button>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { streamAIChat } from '../api/chat'

const BUTTON_SIZE = 72
const EDGE_GAP = 16

const visible = ref(false)
const loading = ref(false)
const question = ref('')
const messageListRef = ref(null)
const messages = ref([])
const position = reactive({ x: EDGE_GAP, y: EDGE_GAP })
const dragState = reactive({
  dragging: false,
  moved: false,
  suppressClick: false,
  offsetX: 0,
  offsetY: 0
})

let nextMessageId = 1

const createWelcomeMessage = () => ({
  id: nextMessageId++,
  role: 'assistant',
  content: '你好，我是社区智能助手。你可以直接问我社区业务相关的问题，我会尽量快速回答你。',
  streaming: false
})

const resetMessages = () => {
  messages.value = [createWelcomeMessage()]
}

resetMessages()

const buttonStyle = computed(() => ({
  left: `${position.x}px`,
  top: `${position.y}px`
}))

const clamp = (value, min, max) => Math.min(Math.max(value, min), max)

const clampPosition = () => {
  const maxX = Math.max(EDGE_GAP, window.innerWidth - BUTTON_SIZE - EDGE_GAP)
  const maxY = Math.max(EDGE_GAP, window.innerHeight - BUTTON_SIZE - EDGE_GAP)
  position.x = clamp(position.x, EDGE_GAP, maxX)
  position.y = clamp(position.y, EDGE_GAP, maxY)
}

const setInitialPosition = () => {
  position.x = Math.max(EDGE_GAP, window.innerWidth - BUTTON_SIZE - 28)
  position.y = Math.max(EDGE_GAP, window.innerHeight - BUTTON_SIZE - 110)
  clampPosition()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (!messageListRef.value) return
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  })
}

const removeDragListeners = () => {
  window.removeEventListener('mousemove', handlePointerMove)
  window.removeEventListener('mouseup', handlePointerUp)
}

const handlePointerMove = (event) => {
  if (!dragState.dragging) return

  const nextX = event.clientX - dragState.offsetX
  const nextY = event.clientY - dragState.offsetY

  if (Math.abs(nextX - position.x) > 2 || Math.abs(nextY - position.y) > 2) {
    dragState.moved = true
  }

  position.x = nextX
  position.y = nextY
  clampPosition()
}

const handlePointerUp = () => {
  if (!dragState.dragging) return

  dragState.dragging = false
  removeDragListeners()

  if (dragState.moved) {
    dragState.suppressClick = true
    setTimeout(() => {
      dragState.suppressClick = false
    }, 0)
  }
}

const handlePointerDown = (event) => {
  if (event.button !== 0) return

  dragState.dragging = true
  dragState.moved = false
  dragState.offsetX = event.clientX - position.x
  dragState.offsetY = event.clientY - position.y

  window.addEventListener('mousemove', handlePointerMove)
  window.addEventListener('mouseup', handlePointerUp)
}

const handleButtonClick = () => {
  if (dragState.suppressClick) {
    dragState.suppressClick = false
    return
  }
  visible.value = !visible.value
  if (visible.value) {
    scrollToBottom()
  }
}

const handleInputKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
}

const handleSend = async () => {
  const content = question.value.trim()
  if (!content || loading.value) return

  const userMessage = {
    id: nextMessageId++,
    role: 'user',
    content,
    streaming: false
  }
  const assistantMessage = reactive({
    id: nextMessageId++,
    role: 'assistant',
    content: '',
    streaming: true
  })

  messages.value.push(userMessage, assistantMessage)
  question.value = ''
  loading.value = true
  scrollToBottom()

  try {
    await streamAIChat(content, {
      onMessage: (chunk) => {
        assistantMessage.content += chunk || ''
        scrollToBottom()
      }
    })

    if (!assistantMessage.content.trim()) {
      assistantMessage.content = '我暂时没有生成有效回复，请稍后重试。'
    }
  } catch (error) {
    const message = error?.message || '聊天失败，请稍后重试。'
    assistantMessage.content = assistantMessage.content.trim() ? assistantMessage.content : message
    ElMessage.error(message)
  } finally {
    assistantMessage.streaming = false
    loading.value = false
    scrollToBottom()
  }
}

const handleResize = () => {
  clampPosition()
}

onMounted(() => {
  setInitialPosition()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  removeDragListeners()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.ai-float-button {
  position: fixed;
  width: 72px;
  height: 72px;
  border-radius: 24px;
  background: linear-gradient(145deg, #1f8cff 0%, #22b8cf 100%);
  box-shadow: 0 18px 40px rgba(31, 140, 255, 0.28);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  user-select: none;
  z-index: 3000;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.ai-float-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 22px 44px rgba(31, 140, 255, 0.34);
}

.bot-shell {
  position: relative;
  width: 48px;
  height: 42px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.96);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.bot-antenna {
  position: absolute;
  top: -8px;
  width: 4px;
  height: 10px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 999px;
}

.bot-antenna::after {
  content: '';
  position: absolute;
  left: 50%;
  top: -4px;
  transform: translateX(-50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ffd166;
}

.bot-face {
  display: flex;
  gap: 10px;
}

.bot-face span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #1f8cff;
}

.bot-mouth {
  margin-top: 8px;
  width: 20px;
  height: 4px;
  border-radius: 999px;
  background: #1f8cff;
  opacity: 0.72;
}

.ai-chat-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(circle at top right, rgba(31, 140, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #f7fbff 0%, #ffffff 100%);
}

.ai-chat-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e6f0ff;
}

.ai-chat-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
}

.ai-chat-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
}

.ai-chat-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 18px 2px 8px;
}

.chat-row {
  display: flex;
  margin-bottom: 14px;
}

.chat-row.user {
  justify-content: flex-end;
}

.chat-row.assistant {
  justify-content: flex-start;
}

.chat-bubble {
  max-width: 88%;
  padding: 12px 14px;
  border-radius: 18px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
}

.chat-row.user .chat-bubble {
  background: linear-gradient(145deg, #1f8cff 0%, #4f9dff 100%);
  color: #ffffff;
  border-bottom-right-radius: 6px;
}

.chat-row.assistant .chat-bubble {
  background: #ffffff;
  color: #1f2937;
  border: 1px solid #e5efff;
  border-bottom-left-radius: 6px;
}

.chat-role {
  margin-bottom: 6px;
  font-size: 12px;
  opacity: 0.72;
}

.chat-content {
  white-space: pre-wrap;
  line-height: 1.7;
  word-break: break-word;
}

.chat-streaming {
  display: flex;
  gap: 4px;
  margin-top: 8px;
}

.chat-streaming span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.4;
  animation: pulse 1s infinite ease-in-out;
}

.chat-streaming span:nth-child(2) {
  animation-delay: 0.15s;
}

.chat-streaming span:nth-child(3) {
  animation-delay: 0.3s;
}

.ai-chat-input {
  padding-top: 14px;
  border-top: 1px solid #e6f0ff;
}

.ai-chat-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
}

.ai-chat-tip {
  font-size: 12px;
  color: #6b7280;
}

@keyframes pulse {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.3;
  }
  40% {
    transform: scale(1);
    opacity: 0.85;
  }
}
</style>
