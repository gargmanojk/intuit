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

            this.isConnected = data.agent_service === 'healthy';
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

        // Show typing indicator
        this.showTypingIndicator();

        try {
            const response = await this.callAgentService(message);
            this.hideTypingIndicator();

            if (response && response.response) {
                this.addMessage(response.response, 'bot', response.confidence);
            } else {
                throw new Error('Invalid response from agent service');
            }
        } catch (error) {
            console.error('Error sending message:', error);
            this.hideTypingIndicator();
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

        return await response.json();
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