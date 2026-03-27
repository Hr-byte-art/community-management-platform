const createChatTraceId = () => `chat-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`

const parseEventBlock = (block) => {
  if (!block) return null

  const lines = block
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(Boolean)

  if (!lines.length) return null

  let eventName = 'message'
  const dataLines = []

  lines.forEach(line => {
    if (line.startsWith('event:')) {
      eventName = line.slice(6).trim() || 'message'
      return
    }
    if (line.startsWith('data:')) {
      dataLines.push(line.slice(5).trimStart())
    }
  })

  return {
    event: eventName,
    data: dataLines.join('\n')
  }
}

const extractErrorMessage = async (response) => {
  const text = await response.text()
  if (!text) return '\u804a\u5929\u8bf7\u6c42\u5931\u8d25'

  try {
    const payload = JSON.parse(text)
    return payload?.message || '\u804a\u5929\u8bf7\u6c42\u5931\u8d25'
  } catch (error) {
    return text
  }
}

export const streamAIChat = async (question, handlers = {}) => {
  const {
    onStart,
    onMessage,
    onDone
  } = handlers

  const traceId = createChatTraceId()
  const token = localStorage.getItem('token')
  const startedAt = Date.now()

  onStart?.({ traceId })
  console.info('[ai-chat][frontend] request-start', {
    traceId,
    url: '/ai/chat/stream',
    startedAt: new Date(startedAt).toISOString()
  })

  const response = await fetch('/api/ai/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      'X-AI-Trace-Id': traceId
    },
    body: JSON.stringify({ question })
  })

  if (response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('permissions')
    window.location.href = '/login'
    throw new Error('\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55')
  }

  if (!response.ok) {
    const message = await extractErrorMessage(response)
    console.warn('[ai-chat][frontend] request-error', {
      traceId,
      status: response.status,
      durationMs: Date.now() - startedAt,
      message
    })
    throw new Error(message)
  }

  if (!response.body) {
    throw new Error('\u6682\u65e0\u6cd5\u8bfb\u53d6\u804a\u5929\u8fd4\u56de')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  let finished = false

  const handleBlock = (block) => {
    const parsed = parseEventBlock(block)
    if (!parsed) return

    if (parsed.event === 'message') {
      onMessage?.(parsed.data)
      return
    }

    if (parsed.event === 'done') {
      finished = true
      return
    }

    if (parsed.event === 'error') {
      throw new Error(parsed.data || '\u667a\u80fd\u52a9\u624b\u6682\u65f6\u65e0\u6cd5\u56de\u590d')
    }
  }

  try {
    while (true) {
      const { value, done } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const blocks = buffer.split(/\r?\n\r?\n/)
      buffer = blocks.pop() || ''
      blocks.forEach(handleBlock)

      if (finished) {
        break
      }
    }

    buffer += decoder.decode()
    if (!finished && buffer.trim()) {
      handleBlock(buffer)
    }

    console.info('[ai-chat][frontend] request-finish', {
      traceId,
      durationMs: Date.now() - startedAt
    })
    onDone?.({ traceId })
    return traceId
  } finally {
    reader.releaseLock()
  }
}
