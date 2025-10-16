import React, { useEffect } from "react";

export default function FacebookLoginButton({ handleLogin }) {
  useEffect(() => {
    // Tạo script SDK
    (function (d, s, id) {
      let js,
        fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) {
        return;
      }
      js = d.createElement(s);
      js.id = id;
      js.src = "https://connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    })(document, "script", "facebook-jssdk");

    // Init khi SDK sẵn sàng
    window.fbAsyncInit = function () {
      window.FB.init({
        appId: "1137527617386740",
        cookie: true,
        xfbml: true,
        version: "v1.0", // hoặc thử v17.0 nếu vẫn lỗi
      });
    };
  }, []);
  return (
    <button
      onClick={handleLogin}
      className="w-full flex items-center justify-center border rounded-md py-2 hover:bg-gray-50"
    >
      <img
        src="https://www.svgrepo.com/show/448224/facebook.svg"
        alt="Facebook"
        className="h-5 w-5 mr-2"
      />
      Continue with Facebook
    </button>
  );
}
