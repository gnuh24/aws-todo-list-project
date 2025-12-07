import React, { useEffect } from "react";
import { toast } from "sonner";

export default function SecureGate({ children }) {
  let user = JSON.parse(localStorage.getItem("USER_INFO"));

  useEffect(() => {
    // Nếu chưa đăng nhập
    if (!user) {
      toast.error("Vui lòng đăng nhập");
      window.location.href = "/login";
      return;
    }

    // Nếu đã đăng nhập → chuyển đến inbox
    if (window.location.pathname === "/") {
      toast.success("Chào mừng trở lại");
      window.location.href = "/app/inbox";
    }
  }, []);

  // Nếu user đã login và không cần redirect, render children
  if (!user) return null;

  return children;
}
