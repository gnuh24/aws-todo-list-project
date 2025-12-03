// components/ModalNotification.jsx
import React, { useState, useEffect } from 'react';
import {useNavigate} from "react-router-dom";

const ModalNotification = ({ message, duration = 5000, setNewNotificationFormWebsocket }) => {
  const [isVisible, setIsVisible] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
      setNewNotificationFormWebsocket(null);
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, setNewNotificationFormWebsocket]);

  if (!isVisible) return null;

  // Xử lý click vào medal (mở trang thông báo)
  const handleMedalClick = () => {
    setIsVisible(false);
    setNewNotificationFormWebsocket(null);
    // Điều hướng đến trang thông báo
    navigate('/app/notifications');
  };

  // Ngăn sự kiện click vào nút × lan ra div cha
  const handleDismissClick = (e) => {
    e.stopPropagation();
    setIsVisible(false);
    setNewNotificationFormWebsocket(null);
  };

  return (
      <div className="fixed top-6 right-6 z-50 max-w-xs w-full animate-slideInUp">
        <div
            onClick={handleMedalClick}
            className="bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-xl shadow-lg border border-white/20 overflow-hidden flex items-center p-4 gap-3 cursor-pointer hover:opacity-90 transition-opacity"
        >
          <div className="text-2xl">🔔</div>
          <p className="text-sm font-medium flex-1">{message.title}</p>
          <button
              onClick={handleDismissClick}
              className="text-white/80 hover:text-white text-xl w-6 h-6 flex items-center justify-center rounded-full hover:bg-white/10 transition"
              aria-label="Đóng thông báo"
          >
            ×
          </button>
        </div>
      </div>
  );
};

export default ModalNotification;