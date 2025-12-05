// TurboTax Web UI - Single Page Application JavaScript

class TurboTaxSPA {
    constructor() {
        this.agentServiceUrl = '/api/chat';
        this.isConnected = false;
        this.currentProvider = 'ollama';
        this.userId = '';
        this.isStreaming = false;

        this.init();
    }

    init() {
        this.bindEvents();
        this.readInitialUserId();
        this.checkConnection();
        setInterval(() => this.checkConnection(), 30000); // Check every 30 seconds
    }

    bindEvents() {
        // Welcome screen
        const startChatBtn = document.getElementById('startChatBtn');
        if (startChatBtn) {
            startChatBtn.addEventListener('click', () => this.showChatInterface());
        }

        // Back to welcome
        const backBtn = document.getElementById('backToWelcome');
        if (backBtn) {
            backBtn.addEventListener('click', () => this.showWelcomeScreen());
        }

        // Chat input
        const messageInput = document.getElementById('messageInput');
        const sendButton = document.getElementById('sendButton');

        if (messageInput) {
            messageInput.addEventListener('input', () => this.updateSendButton());
            messageInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            });
        }

        if (sendButton) {
            sendButton.addEventListener('click', () => this.sendMessage());
        }

        // Provider selection
        const providerSelect = document.getElementById('providerSelect');
        if (providerSelect) {
            providerSelect.addEventListener('change', (e) => {
                this.currentProvider = e.target.value;
            });
        }

        // User ID input
        const userIdInput = document.getElementById('userIdInput');
        if (userIdInput) {
            userIdInput.addEventListener('input', (e) => {
                this.userId = e.target.value.trim();
            });
        }

        // Quick action buttons
        const quickButtons = document.querySelectorAll('.quick-action-btn');
        quickButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const query = btn.dataset.query;
                if (query) {
                    this.sendQuickMessage(query);
                }
            });
        });

        // Streaming toggle
        const streamingToggle = document.getElementById('streamingToggle');
        if (streamingToggle) {
            streamingToggle.addEventListener('change', (e) => {
                this.isStreaming = e.target.checked;
            });
        }

        // Error modal
        const modalClose = document.querySelector('.modal-close');
        const retryBtn = document.getElementById('retryConnection');

        if (modalClose) {
            modalClose.addEventListener('click', () => this.hideErrorModal());
        }

        if (retryBtn) {
            retryBtn.addEventListener('click', () => {
                this.hideErrorModal();
                this.checkConnection();
            });
        }
    }

    async checkConnection() {
        try {
            const response = await fetch('/api/health');
            const data = await response.json();

            this.isConnected = data.web_ui === 'healthy';
            this.updateConnectionStatus();
        } catch (error) {
            console.error('Connection check failed:', error);
            this.isConnected = false;
            this.updateConnectionStatus();
        }
    }

    readInitialUserId() {
        const userIdInput = document.getElementById('userIdInput');
        if (userIdInput && userIdInput.value.trim()) {
            this.userId = userIdInput.value.trim();
        }
    }

    updateConnectionStatus() {
        const statusElement = document.getElementById('connectionStatus');
        if (statusElement) {
            statusElement.textContent = this.isConnected ? 'Connected' : 'Connecting...';
            statusElement.className = this.isConnected ? 'status-connected' : 'status-connecting';
        }
    }

    showChatInterface() {
        // Read the current user ID from the input field before showing chat
        const userIdInput = document.getElementById('userIdInput');
        if (userIdInput) {
            this.userId = userIdInput.value.trim() || 'user123';
        }

        document.getElementById('welcomeScreen').classList.add('hidden');
        document.getElementById('chatInterface').classList.remove('hidden');
        document.getElementById('messageInput').focus();
    }

    showWelcomeScreen() {
        document.getElementById('chatInterface').classList.add('hidden');
        document.getElementById('welcomeScreen').classList.remove('hidden');
    }

    updateSendButton() {
        const messageInput = document.getElementById('messageInput');
        const sendButton = document.getElementById('sendButton');
        const hasText = messageInput.value.trim().length > 0;

        sendButton.disabled = !hasText;
        sendButton.style.opacity = hasText ? '1' : '0.5';
    }

    async sendMessage() {
        const messageInput = document.getElementById('messageInput');
        const message = messageInput.value.trim();

        if (!message) return;

        // Add user message to chat
        this.addMessage(message, 'user');
        messageInput.value = '';
        this.updateSendButton();

        // Show typing indicator (only for non-streaming)
        if (!this.isStreaming) {
            this.showTypingIndicator();
        }

        try {
            const response = await this.callAgentService(message);

            if (!this.isStreaming) {
                this.hideTypingIndicator();

                if (response && response.response) {
                    this.addMessage(response.response, 'bot', response.confidence);
                } else {
                    throw new Error('Invalid response from agent service');
                }
            }
            // For streaming responses, the UI updates are handled in handleStreamingResponse

        } catch (error) {
            console.error('Error sending message:', error);
            if (!this.isStreaming) {
                this.hideTypingIndicator();
            }
            this.addMessage('Sorry, I encountered an error. Please try again later.', 'bot');
            this.showErrorModal('Failed to get response from the tax assistant.');
        }
    }

    async sendQuickMessage(query) {
        // Set the input value and send
        document.getElementById('messageInput').value = query;
        this.updateSendButton();
        await this.sendMessage();
    }

    async callAgentService(query) {
        const payload = {
            user_id: this.userId,
            query: query,
            provider: this.currentProvider,
            stream: this.isStreaming
        };

        console.log('Sending request with user_id:', this.userId, 'payload:', payload);

        const response = await fetch(this.agentServiceUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        // Handle streaming responses
        if (this.isStreaming) {
            return await this.handleStreamingResponse(response);
        }

        // Handle regular JSON responses
        return await response.json();
    }

    async handleStreamingResponse(response) {
        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let fullMessage = '';
        let confidence = null;

        try {
            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                const chunk = decoder.decode(value, { stream: true });
                const lines = chunk.split('\n');

                for (const line of lines) {
                    if (line.startsWith('data: ')) {
                        const data = line.slice(6); // Remove 'data: ' prefix

                        if (data === '[DONE]') {
                            // Stream completed
                            break;
                        } else if (data.trim()) {
                            // Add chunk to message
                            fullMessage += data;
                            // Update the UI with the current message
                            this.updateStreamingMessage(fullMessage);
                        }
                    }
                }
            }

            // Hide typing indicator and finalize message
            this.hideTypingIndicator();
            this.finalizeStreamingMessage(fullMessage, confidence);

            return { response: fullMessage, confidence: confidence || 0.85 };

        } catch (error) {
            console.error('Error handling streaming response:', error);
            this.hideTypingIndicator();
            throw error;
        }
    }

    updateStreamingMessage(text) {
        // Update or create the streaming message in the UI
        let streamingMessageDiv = document.getElementById('streaming-message');
        if (!streamingMessageDiv) {
            streamingMessageDiv = document.createElement('div');
            streamingMessageDiv.id = 'streaming-message';
            streamingMessageDiv.className = 'message bot-message streaming';

            const avatarDiv = document.createElement('div');
            avatarDiv.className = 'message-avatar';
            const avatarIcon = document.createElement('i');
            avatarIcon.className = 'fas fa-robot';
            avatarDiv.appendChild(avatarIcon);

            const contentDiv = document.createElement('div');
            contentDiv.className = 'message-content';

            const textDiv = document.createElement('div');
            textDiv.className = 'message-text';
            textDiv.id = 'streaming-text';

            contentDiv.appendChild(textDiv);
            streamingMessageDiv.appendChild(avatarDiv);
            streamingMessageDiv.appendChild(contentDiv);

            const messagesContainer = document.getElementById('chatMessages');
            messagesContainer.appendChild(streamingMessageDiv);
        }

        const textDiv = document.getElementById('streaming-text');
        textDiv.textContent = text;
        document.getElementById('chatMessages').scrollTop = document.getElementById('chatMessages').scrollHeight;
    }

    finalizeStreamingMessage(text, confidence) {
        const streamingMessageDiv = document.getElementById('streaming-message');
        if (streamingMessageDiv) {
            // Remove streaming class and add final styling
            streamingMessageDiv.classList.remove('streaming');
            streamingMessageDiv.id = '';

            // Add confidence and timestamp
            const contentDiv = streamingMessageDiv.querySelector('.message-content');
            if (confidence !== null) {
                const confidenceDiv = document.createElement('div');
                confidenceDiv.className = 'message-confidence';
                confidenceDiv.textContent = `Confidence: ${(confidence * 100).toFixed(1)}%`;
                contentDiv.appendChild(confidenceDiv);
            }

            const timeDiv = document.createElement('div');
            timeDiv.className = 'message-time';
            timeDiv.textContent = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            contentDiv.appendChild(timeDiv);
        }
    }

    addMessage(text, sender, confidence = null) {
        const messagesContainer = document.getElementById('chatMessages');
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${sender}-message`;

        const avatarDiv = document.createElement('div');
        avatarDiv.className = 'message-avatar';

        const avatarIcon = document.createElement('i');
        avatarIcon.className = sender === 'user' ? 'fas fa-user' : 'fas fa-robot';
        avatarDiv.appendChild(avatarIcon);

        const contentDiv = document.createElement('div');
        contentDiv.className = 'message-content';

        const textDiv = document.createElement('div');
        textDiv.className = 'message-text';
        textDiv.textContent = text;

        contentDiv.appendChild(textDiv);

        if (confidence !== null && sender === 'bot') {
            const confidenceDiv = document.createElement('div');
            confidenceDiv.className = 'message-confidence';
            confidenceDiv.textContent = `Confidence: ${(confidence * 100).toFixed(1)}%`;
            contentDiv.appendChild(confidenceDiv);
        }

        const timeDiv = document.createElement('div');
        timeDiv.className = 'message-time';
        timeDiv.textContent = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        contentDiv.appendChild(timeDiv);

        messageDiv.appendChild(avatarDiv);
        messageDiv.appendChild(contentDiv);

        messagesContainer.appendChild(messageDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    showTypingIndicator() {
        const typingDiv = document.getElementById('typingIndicator');
        typingDiv.classList.remove('hidden');
        document.getElementById('chatMessages').scrollTop = document.getElementById('chatMessages').scrollHeight;
    }

    hideTypingIndicator() {
        const typingDiv = document.getElementById('typingIndicator');
        typingDiv.classList.add('hidden');
    }

    showErrorModal(message) {
        const modal = document.getElementById('errorModal');
        const errorMessage = document.getElementById('errorMessage');
        errorMessage.textContent = message;
        modal.classList.remove('hidden');
    }

    hideErrorModal() {
        const modal = document.getElementById('errorModal');
        modal.classList.add('hidden');
    }
}

// Initialize the SPA when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TurboTaxSPA();
});