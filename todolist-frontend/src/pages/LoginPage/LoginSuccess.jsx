import React, { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

export default function LoginSuccess() {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");

    if (token) {
      try {
        localStorage.setItem("ACCESS_TOKEN", token);

        const decoded = jwtDecode(token);
        console.log("Decoded token:", decoded);

        const userInfo = {
          name: decoded.sub || decoded.fullName || "",
          email: decoded.email || "",
          picture: decoded.picture || decoded.avatar || "",
        };

        localStorage.setItem("USER_INFO", JSON.stringify(userInfo));

        navigate("/app/inbox");
      } catch (err) {
        console.error("Decode token error:", err);
        navigate("/login");
      }
    } else {
      navigate("/login");
    }
  }, [location, navigate]);

  return (
    <div className="flex items-center justify-center h-screen bg-gray-900 text-white text-xl">
      Đang đăng nhập bằng Google...
    </div>
  );
}
