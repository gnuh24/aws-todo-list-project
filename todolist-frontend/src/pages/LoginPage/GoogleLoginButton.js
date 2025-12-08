import React from "react";

export default function GoogleLoginButton() {
  const handleGoogleLogin = () => {
    window.location.href =
      "http://localhost:9999/api/auth/oauth2/authorization/google";
  };

  return (
    <button
      onClick={handleGoogleLogin}
      className="flex items-center justify-center w-full py-2 mt-3 text-black  hover:bg-gray-50 rounded-lg shadow"
    >
      <img
        src="https://developers.google.com/identity/images/g-logo.png"
        alt="Google"
        className="w-5 h-5 mr-2"
      />
      Đăng nhập bằng Google
    </button>
  );
}
