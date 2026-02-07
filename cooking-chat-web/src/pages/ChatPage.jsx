import React, { useState, useEffect, useRef } from 'react';
import ConfirmDialog from '../components/ConfirmDialog';
import './ChatPage.css';

const ChatPage = () => {
  const [conversations, setConversations] = useState([]);
  const [currentConversation, setCurrentConversation] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [editingConversationId, setEditingConversationId] = useState(null);
  const [editingTitle, setEditingTitle] = useState('');
  const [deleteConfirmDialog, setDeleteConfirmDialog] = useState({ isOpen: false, conversationId: null });
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem('userInfo'));
    setUserInfo(user);
    loadConversations();
  }, []);

  useEffect(() => {
    // 如果加载完会话列表后没有会话，自动创建默认对话
    if (conversations.length === 0 && userInfo && !localStorage.getItem('conversationsLoaded')) {
      createDefaultConversation();
      localStorage.setItem('conversationsLoaded', 'true');
    } else if (conversations.length > 0 && !currentConversation) {
      // 如果有会话但没有选中，自动选中第一个
      selectConversation(conversations[0]);
    }
  }, [conversations, userInfo]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const getAuthHeaders = () => ({
    'Content-Type': 'application/json',
    'token': localStorage.getItem('token')
  });

  const loadConversations = async () => {
    try {
      const response = await fetch('/api/conversation/list', {
        headers: getAuthHeaders()
      });
      const result = await response.json();
      if (result.code === 200) {
        setConversations(result.data || []);
      }
    } catch (error) {
      console.error('加载会话列表失败', error);
    }
  };

  const createDefaultConversation = async () => {
    try {
      const response = await fetch('/api/conversation/create', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ title: '默认对话' })
      });
      const result = await response.json();
      if (result.code === 200) {
        setConversations([result.data]);
        selectConversation(result.data);
      }
    } catch (error) {
      console.error('创建默认会话失败', error);
    }
  };

  const loadMessages = async (conversationId) => {
    try {
      const response = await fetch(`/api/conversation/${conversationId}/messages`, {
        headers: getAuthHeaders()
      });
      const result = await response.json();
      if (result.code === 200) {
        setMessages(result.data || []);
      }
    } catch (error) {
      console.error('加载消息失败', error);
    }
  };

  const createNewConversation = async () => {
    if (conversations.length >= 10) {
      alert('会话数量已达上限（10个），请删除旧会话后再创建');
      return;
    }

    try {
      const response = await fetch('/api/conversation/create', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ title: '新对话' })
      });
      const result = await response.json();
      if (result.code === 200) {
        setConversations([result.data, ...conversations]);
        selectConversation(result.data);
      }
    } catch (error) {
      console.error('创建会话失败', error);
    }
  };

  const selectConversation = (conversation) => {
    setCurrentConversation(conversation);
    loadMessages(conversation.id);
  };

  const deleteConversation = async (conversationId, e) => {
    e.stopPropagation();
    setDeleteConfirmDialog({ isOpen: true, conversationId });
  };

  const confirmDelete = async () => {
    const conversationId = deleteConfirmDialog.conversationId;
    setDeleteConfirmDialog({ isOpen: false, conversationId: null });

    try {
      const response = await fetch(`/api/conversation/${conversationId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
      });
      const result = await response.json();
      if (result.code === 200) {
        setConversations(conversations.filter(c => c.id !== conversationId));
        if (currentConversation?.id === conversationId) {
          setCurrentConversation(null);
          setMessages([]);
        }
      }
    } catch (error) {
      console.error('删除会话失败', error);
    }
  };

  const cancelDelete = () => {
    setDeleteConfirmDialog({ isOpen: false, conversationId: null });
  };

  const startEditTitle = (conversation, e) => {
    e.stopPropagation();
    setEditingConversationId(conversation.id);
    setEditingTitle(conversation.title);
  };

  const saveTitle = async (conversationId) => {
    if (!editingTitle.trim()) {
      setEditingConversationId(null);
      return;
    }

    try {
      const response = await fetch(`/api/conversation/${conversationId}/title`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify({ title: editingTitle })
      });
      const result = await response.json();
      if (result.code === 200) {
        setConversations(conversations.map(conv =>
          conv.id === conversationId ? { ...conv, title: editingTitle } : conv
        ));
        if (currentConversation?.id === conversationId) {
          setCurrentConversation({ ...currentConversation, title: editingTitle });
        }
      }
    } catch (error) {
      console.error('更新标题失败', error);
    } finally {
      setEditingConversationId(null);
    }
  };

  const cancelEditTitle = () => {
    setEditingConversationId(null);
    setEditingTitle('');
  };

  const sendMessage = async () => {
    if (!inputMessage.trim() || !currentConversation) return;
    if (loading) return;

    const userMessage = {
      conversationId: currentConversation.id,
      sender: 'USER',
      content: inputMessage,
      messageType: 'TEXT',
      sendTime: new Date().toISOString()
    };

    setMessages([...messages, userMessage]);
    const questionText = inputMessage;
    setInputMessage('');
    setLoading(true);

    // 如果是第一条消息，更新会话标题
    const isFirstMessage = messages.length === 0;

    try {
      // 保存用户消息
      await fetch('/api/conversation/message', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(userMessage)
      });

      // 如果是第一条消息，更新会话标题
      if (isFirstMessage) {
        const title = questionText.length > 20 
          ? questionText.substring(0, 20) + '...' 
          : questionText;
        
        await fetch(`/api/conversation/${currentConversation.id}/title`, {
          method: 'PUT',
          headers: getAuthHeaders(),
          body: JSON.stringify({ title })
        });

        // 更新本地会话列表
        setConversations(conversations.map(conv => 
          conv.id === currentConversation.id 
            ? { ...conv, title } 
            : conv
        ));
        setCurrentConversation({ ...currentConversation, title });
      }

      // 使用EventSource调用AI流式接口
      const token = localStorage.getItem('token');
      const url = `/api/chat/stream?userId=${userInfo.id}&conversationId=${currentConversation.id}&question=${encodeURIComponent(questionText)}&stream=true&token=${encodeURIComponent(token)}`;
      const eventSource = new EventSource(url);

      let aiResponse = '';
      const aiMessage = {
        conversationId: currentConversation.id,
        sender: 'AI',
        content: '',
        messageType: 'TEXT',
        sendTime: new Date().toISOString()
      };

      setMessages(prev => [...prev, aiMessage]);

      // 监听message事件（AI回复的内容片段）
      eventSource.addEventListener('message', (event) => {
        const data = event.data;
        if (data && data !== '[DONE]') {
          aiResponse += data;
          setMessages(prev => {
            const newMessages = [...prev];
            newMessages[newMessages.length - 1] = {
              ...aiMessage,
              content: aiResponse
            };
            return newMessages;
          });
        }
      });

      // 监听complete事件（回答完成）
      eventSource.addEventListener('complete', (event) => {
        console.log('AI回答完成:', event.data);
        eventSource.close();
        setLoading(false);
        
        // 保存AI消息
        if (aiResponse) {
          fetch('/api/conversation/message', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({
              ...aiMessage,
              content: aiResponse
            })
          });
        }
      });

      // 监听error事件（错误处理）
      eventSource.addEventListener('error', (event) => {
        console.error('SSE错误:', event);
        eventSource.close();
        setLoading(false);
        
        if (!aiResponse) {
          setMessages(prev => {
            const newMessages = [...prev];
            if (newMessages[newMessages.length - 1].sender === 'AI') {
              newMessages[newMessages.length - 1].content = '抱歉，发生了错误，请重试';
            }
            return newMessages;
          });
        }
      });

      // EventSource自身的错误处理
      eventSource.onerror = (error) => {
        console.error('EventSource连接错误:', error);
        eventSource.close();
        setLoading(false);
        
        if (!aiResponse) {
          setMessages(prev => {
            const newMessages = [...prev];
            if (newMessages[newMessages.length - 1].sender === 'AI') {
              newMessages[newMessages.length - 1].content = '抱歉，连接失败，请重试';
            }
            return newMessages;
          });
        }
      };

    } catch (error) {
      console.error('发送消息失败', error);
      setLoading(false);
      setMessages(prev => {
        const newMessages = [...prev];
        if (newMessages[newMessages.length - 1].sender === 'AI') {
          newMessages[newMessages.length - 1].content = '抱歉，发生了错误';
        }
        return newMessages;
      });
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    window.location.reload();
  };

  return (
    <div className="chat-page">
      <ConfirmDialog
        isOpen={deleteConfirmDialog.isOpen}
        title="删除会话"
        message="确定要删除这个会话吗？删除后将无法恢复，所有消息记录都会丢失。"
        onConfirm={confirmDelete}
        onCancel={cancelDelete}
      />
      
      <div className="sidebar">
        <div className="sidebar-header">
          <h3>对话历史</h3>
          <button onClick={createNewConversation} className="btn-new">
            + 新对话
          </button>
        </div>
        <div className="conversation-list">
          {conversations.map(conv => (
            <div
              key={conv.id}
              className={`conversation-item ${currentConversation?.id === conv.id ? 'active' : ''}`}
              onClick={() => selectConversation(conv)}
            >
              {editingConversationId === conv.id ? (
                <input
                  type="text"
                  className="conversation-title-input"
                  value={editingTitle}
                  onChange={(e) => setEditingTitle(e.target.value)}
                  onBlur={() => saveTitle(conv.id)}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      saveTitle(conv.id);
                    } else if (e.key === 'Escape') {
                      cancelEditTitle();
                    }
                  }}
                  onClick={(e) => e.stopPropagation()}
                  autoFocus
                />
              ) : (
                <>
                  <div 
                    className="conversation-title"
                    onDoubleClick={(e) => startEditTitle(conv, e)}
                  >
                    {conv.title}
                  </div>
                  <div className="conversation-actions">
                    <button
                      className="btn-edit"
                      onClick={(e) => startEditTitle(conv, e)}
                      title="编辑标题"
                    >
                      ✎
                    </button>
                    <button
                      className="btn-delete"
                      onClick={(e) => deleteConversation(conv.id, e)}
                      title="删除会话"
                    >
                      ×
                    </button>
                  </div>
                </>
              )}
            </div>
          ))}
        </div>
        <div className="sidebar-footer">
          <div className="user-info">
            <span>{userInfo?.nickName}</span>
            <button onClick={handleLogout} className="btn-logout">退出</button>
          </div>
        </div>
      </div>

      <div className="chat-main">
        {currentConversation ? (
          <>
            <div className="chat-header">
              <h3>{currentConversation.title}</h3>
            </div>
            <div className="messages-container">
              {messages.map((msg, index) => (
                <div key={index} className={`message ${msg.sender.toLowerCase()}`}>
                  <div className="message-content">{msg.content}</div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>
            <div className="input-container">
              <textarea
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="输入消息... (Enter发送，Shift+Enter换行)"
                disabled={loading}
              />
              <button onClick={sendMessage} disabled={loading || !inputMessage.trim()}>
                {loading ? '发送中...' : '发送'}
              </button>
            </div>
          </>
        ) : (
          <div className="empty-state">
            <p>请选择或创建一个对话</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatPage;
