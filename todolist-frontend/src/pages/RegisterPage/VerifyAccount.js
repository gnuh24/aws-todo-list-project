import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { Result, Spin, message } from "antd";
import axios from "axios";

export default function VerifyAccount() {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState("loading"); // 'loading' | 'success' | 'error'
  const otp = searchParams.get("otp");
  const storedUser = JSON.parse(localStorage.getItem("USER_REGISTER"));
  const { email } = storedUser;
  console.log(email);

  useEffect(() => {
    const verifyAccount = async () => {
      if (!otp) {
        message.error("Thiếu mã xác thực (OTP).");
        setStatus("error");
        return;
      }

      try {
        const response = await axios.post(
          `http://localhost:9999/api/auth/v1/active-account`,
          null, // body trống
          {
            params: { otp },
          }
        );

        console.log(" Verify success:", response.data);
        message.success("Xác thực tài khoản thành công!");
        setStatus("success");

        // Chuyển hướng về trang đăng nhập sau 3 giây
        setTimeout(() => {
          window.location.href = "/login";
        }, 3000);
      } catch (error) {
        console.error("Verify failed:", error.response?.data || error.message);
        message.error(
          error.response?.data?.message ||
            "Liên kết xác thực không hợp lệ hoặc đã hết hạn."
        );
        setStatus("error");
      }
    };

    verifyAccount();
  }, [otp]);

  //  Loading UI
  if (status === "loading") {
    return (
      <div className="flex justify-center items-center h-screen">
        <Spin size="large" tip="Đang xác thực tài khoản..." />
      </div>
    );
  }

  // Thành công
  if (status === "success") {
    return (
      <Result
        status="success"
        title="Tài khoản đã được kích hoạt!"
        subTitle="Bạn sẽ được chuyển đến trang đăng nhập trong giây lát."
      />
    );
  }

  //  Lỗi
  return (
    <Result
      status="error"
      title="Xác thực thất bại!"
      subTitle="Liên kết không hợp lệ hoặc đã hết hạn."
    />
  );
}
