import {
  ArrowLeftOutlined,
  EyeInvisibleOutlined,
  EyeTwoTone,
} from "@ant-design/icons";
import { Input, Modal, message } from "antd";
import { useState } from "react";
import { toast } from "sonner";
import { https_authupdate } from "../../service/api";

export default function ChangeEmail({ onBack }) {
  const dataUser = JSON.parse(localStorage.getItem("USER_INFO")) || {};
  const currentEmail = dataUser.email;

  const [email1, setEmail1] = useState("");
  const [email2, setEmail2] = useState("");
  const [password, setPassword] = useState("");

  // OTP modal
  const [otpModal, setOtpModal] = useState(false);
  const [otp, setOtp] = useState("");

  const handleSendOtp = async () => {
    if (!email1 || !email2 || email1 !== email2) {
      return toast.error("Emails do not match!");
    }

    if (!password) {
      return toast.error("Please enter your password.");
    }

    try {
      // 1️⃣ CHECK EMAIL TỒN TẠI
      const check = await https_authupdate.get(
        `/v1/check-email?email=${email1}`
      );

      if (check.data?.data === true) {
        return toast.error("Email already exists. Please use another email.");
      }

      // 2️⃣ GỬI OTP
      await https_authupdate.post(`/v1/send-update-email-otp/${email1}`);

      setOtpModal(true);
      message.success("OTP has been sent to your new email.");
    } catch (err) {
      console.error(err);
      toast.error("Failed to send OTP.");
    }
  };

  const handleSubmitOtp = async () => {
    if (!otp) return toast.error("Please enter the OTP.");

    try {
      await https_authupdate.patch("/v1/update-email", {
        otp: otp,
        currentPassword: password,
        newEmail: email1,
      });

      // Update localStorage
      const updated = { ...dataUser, email: email1 };
      localStorage.setItem("USER_INFO", JSON.stringify(updated));

      message.success("Email updated successfully!");
      setOtpModal(false);
      onBack();
    } catch (err) {
      console.error(err);
      toast.error("Invalid OTP or password.");
    }
  };

  return (
    <div className="text-gray-700">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <button onClick={onBack} className="p-1 rounded hover:bg-gray-200">
          <ArrowLeftOutlined />
        </button>
        <h2 className="text-xl font-semibold">Change email address</h2>
      </div>

      {/* Description */}
      <p className="text-sm mb-8">
        Update the email you use for your Todoist account. Your email is
        currently <span className="font-medium">{currentEmail}</span>.
      </p>

      {/* New email */}
      <div className="mb-6">
        <h3 className="text-sm font-medium mb-1">New email</h3>

        <Input
          value={email1}
          onChange={(e) => setEmail1(e.target.value)}
          className="w-80"
        />
      </div>

      {/* Confirm new email */}
      <div className="mb-6">
        <h3 className="text-sm font-medium mb-1">Confirm new email</h3>

        <Input
          value={email2}
          onChange={(e) => setEmail2(e.target.value)}
          className="w-80"
        />
      </div>

      {/* Password */}
      <div className="mb-6">
        <h3 className="text-sm font-medium mb-1">Todoist password</h3>

        <Input.Password
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-80"
          iconRender={(visible) =>
            visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
          }
        />

        <button className="text-sm text-red-500 mt-2 hover:underline">
          Forgot password?
        </button>
      </div>

      {/* Footer buttons */}
      <div className="flex justify-end gap-3 pr-3 mt-10">
        <button
          onClick={onBack}
          className="px-4 py-1.5 rounded border text-sm hover:bg-gray-100"
        >
          Cancel
        </button>

        <button
          onClick={handleSendOtp}
          className="px-4 py-1.5 rounded bg-[#f8b4a0] text-white text-sm hover:bg-[#f7a58d]"
        >
          Change email
        </button>
      </div>

      {/* OTP Modal */}
      <Modal
        title="Enter OTP"
        open={otpModal}
        onCancel={() => setOtpModal(false)}
        onOk={handleSubmitOtp}
        okText="Confirm"
        cancelText="Cancel"
      >
        <p className="mb-2 text-sm">
          Enter the OTP sent to your new email address:
        </p>
        <Input
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          placeholder="Enter OTP"
        />
      </Modal>
    </div>
  );
}
