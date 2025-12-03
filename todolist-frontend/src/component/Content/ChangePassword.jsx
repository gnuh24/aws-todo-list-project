import {
  ArrowLeftOutlined,
  EyeInvisibleOutlined,
  EyeTwoTone,
} from "@ant-design/icons";
import { Input, Button, message } from "antd";
import { useState } from "react";
import { toast } from "sonner";
import { https_authupdate } from "../../service/api";

export default function ChangePassword({ onBack }) {
  const [current, setCurrent] = useState("");
  const [newPw, setNewPw] = useState("");
  const [confirm, setConfirm] = useState("");
  const handleChangePassword = async () => {
    if (!current || !newPw || !confirm) {
      return toast.error("Please fill in all fields.");
    }

    if (newPw.length < 6) {
      return toast.error("New password must be at least 6 characters.");
    }

    if (newPw !== confirm) {
      return toast.error("New passwords do not match.");
    }

    try {
      const res = await https_authupdate.patch("/v1/update-password", {
        oldPassword: current,
        newPassword: newPw,
      });

      toast.success("Password updated successfully!");

      // Nếu backend trả token mới thì cập nhật
      if (res.data?.data?.token) {
        const user = JSON.parse(localStorage.getItem("USER_INFO")) || {};
        user.token = res.data.data.token;
        localStorage.setItem("USER_INFO", JSON.stringify(user));
      }

      // Reset input
      setCurrent("");
      setNewPw("");
      setConfirm("");
    } catch (err) {
      console.error(err);

      if (err.response?.status === 400) {
        return toast.error("Incorrect old password.");
      }

      toast.error("Failed to update password.");
    }
  };

  return (
    <div className="text-gray-700">
      {/* HEADER */}
      <div className="flex items-center gap-3 mb-8">
        <button onClick={onBack} className="p-1 rounded hover:bg-gray-200">
          <ArrowLeftOutlined />
        </button>

        <h2 className="text-xl font-semibold">Change password</h2>
      </div>

      {/* CURRENT PASSWORD */}
      <div className="mb-6">
        <h3 className="text-sm font-medium mb-1">Current password</h3>

        <Input.Password
          value={current}
          onChange={(e) => setCurrent(e.target.value)}
          className="w-80"
          iconRender={(visible) =>
            visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
          }
        />

        <button className="text-sm text-red-500 mt-2 hover:underline">
          Forgot password?
        </button>
      </div>

      {/* NEW PASSWORD */}
      <div className="mb-6">
        <h3 className="text-sm font-medium mb-1">New password</h3>

        <Input.Password
          value={newPw}
          onChange={(e) => setNewPw(e.target.value)}
          className="w-80"
          iconRender={(visible) =>
            visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
          }
        />
      </div>

      {/* CONFIRM NEW PASSWORD */}
      <div className="mb-6">
        <h3 className="text-sm font-medium mb-1">Confirm new password</h3>

        <Input.Password
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          className="w-80"
          iconRender={(visible) =>
            visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
          }
        />
      </div>

      {/* FOOTER */}
      <p className="text-xs text-gray-500 mb-10">
        Your password must be at least 8 characters long. Avoid common words or
        patterns.
      </p>

      <div className="flex justify-end gap-3 pr-3">
        <button
          onClick={onBack}
          className="px-4 py-1.5 rounded border text-sm hover:bg-gray-100"
        >
          Cancel
        </button>

        <button
          onClick={handleChangePassword}
          className="px-4 py-1.5 rounded bg-[#f8b4a0] text-white text-sm hover:bg-[#f7a58d]"
        >
          Change password
        </button>
      </div>
    </div>
  );
}
