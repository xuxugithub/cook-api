import React, { useState, useEffect } from 'react';
import Login from './pages/Login';
import Register from './pages/Register';
import ChatPage from './pages/ChatPage';

const App = () => {
  const [view, setView] = useState('login'); // 'login', 'register', 'chat'
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsAuthenticated(true);
      setView('chat');
    }
  }, []);

  const handleLoginSuccess = (data) => {
    setIsAuthenticated(true);
    setView('chat');
  };

  const handleRegisterSuccess = (data) => {
    setIsAuthenticated(true);
    setView('chat');
  };

  if (view === 'chat' && isAuthenticated) {
    return <ChatPage />;
  }

  if (view === 'register') {
    return (
      <Register
        onRegisterSuccess={handleRegisterSuccess}
        onSwitchToLogin={() => setView('login')}
      />
    );
  }

  return (
    <Login
      onLoginSuccess={handleLoginSuccess}
      onSwitchToRegister={() => setView('register')}
    />
  );
};

export default App;
