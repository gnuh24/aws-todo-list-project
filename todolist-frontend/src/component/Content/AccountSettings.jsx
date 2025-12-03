import { useState } from "react";
import { Switch } from "antd";
import { https_user } from "../../service/api";

export default function AccountSettings({
  onGotoChangePassword,
  onGotoChangeEmail,
}) {
  const dataUser = JSON.parse(localStorage.getItem("USER_INFO")) || {};
  const { displayName, email, avatarUrl } = dataUser;

  const [name, setName] = useState(displayName || "");
  const [tempName, setTempName] = useState(displayName || "");
  const [editing, setEditing] = useState(false);

  const handleCancel = () => {
    setTempName(name);
    setEditing(false);
  };

  const handleUpdate = async () => {
    try {
      setEditing(false);

      const payload = {
        avatar: avatarUrl || null, // hoặc state bạn đang dùng để lưu avatar
        displayName: tempName,
      };

      // Gọi API cập nhật
      const res = await https_user.patch(`/v1/accounts/me`, payload);

      // Cập nhật lại UI
      setName(tempName);

      // Lưu lại localStorage
      const updated = { ...dataUser, displayName: tempName };
      localStorage.setItem("USER_INFO", JSON.stringify(updated));

      console.log("Update success:", res.data);
    } catch (error) {
      console.error("Update failed:", error);
    }
  };

  return (
    <div className="text-gray-700">
      {/* HEADER */}
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold">Account</h2>
        <button className="px-4 py-1.5 rounded border text-sm hover:bg-gray-100">
          Manage plan
        </button>
      </div>

      {/* PLAN */}
      <div className="mb-8">
        <h3 className="text-sm text-gray-500">Plan</h3>
        <p className="text-lg font-medium">Beginner</p>
      </div>

      {/* PHOTO */}
      <div className="mb-8">
        <h3 className="text-sm text-gray-500 mb-2">Photo</h3>

        <div className="flex items-center gap-5">
          <img
            src={avatarUrl || "https://i.pravatar.cc/80"}
            className="w-20 h-20 rounded-full object-cover"
          />

          <div className="flex gap-3">
            <button className="px-3 py-1 rounded border text-sm hover:bg-gray-100">
              Change photo
            </button>

            <button className="px-3 py-1 rounded border border-red-400 text-red-500 text-sm hover:bg-red-50">
              Remove photo
            </button>
          </div>
        </div>

        <p className="text-xs text-gray-500 mt-2">
          Pick a photo up to 4MB. Your avatar photo will be public.
        </p>
      </div>

      {/* NAME */}
      <div className="mb-8">
        <h3 className="text-sm text-gray-500 mb-1">Name</h3>

        <input
          value={tempName}
          onChange={(e) => setTempName(e.target.value)}
          onFocus={() => setEditing(true)}
          className="border border-gray-300 px-3 py-2 rounded w-80 text-sm
                     focus:outline-none focus:border-gray-400"
        />

        <p className="text-xs text-gray-500 mt-1">{tempName.length}/255</p>

        {/* Buttons appear only when editing */}
        {editing && (
          <div className="flex gap-2 mt-3">
            <button
              onClick={handleCancel}
              className="px-3 py-1 rounded border text-sm hover:bg-gray-100"
            >
              Cancel
            </button>

            <button
              onClick={handleUpdate}
              disabled={tempName.trim() === "" || tempName === name}
              className="px-3 py-1 rounded bg-red-500 text-white text-sm
                         disabled:opacity-50"
            >
              Update
            </button>
          </div>
        )}
      </div>

      {/* EMAIL */}
      <div className="mb-8">
        <h3 className="text-sm text-gray-500 mb-1">Email</h3>
        <p className="mb-2 text-sm">{email}</p>

        <button
          onClick={onGotoChangeEmail}
          className="px-3 py-1 rounded border text-sm hover:bg-gray-100"
        >
          Change email
        </button>
      </div>

      {/* PASSWORD */}
      <div className="mb-8">
        <h3 className="text-sm text-gray-500 mb-1">Password</h3>

        <button
          onClick={onGotoChangePassword}
          className="px-3 py-1 rounded border text-sm hover:bg-gray-100"
        >
          Change password
        </button>
      </div>

      {/* 2FA */}
      <div className="mb-8">
        <h3 className="text-sm text-gray-500 mb-1">
          Two-factor authentication
        </h3>

        <Switch defaultChecked={false} />

        <p className="text-xs text-gray-500 mt-1">
          2FA is disabled on your Todoist account.
        </p>
      </div>
    </div>
  );
}
