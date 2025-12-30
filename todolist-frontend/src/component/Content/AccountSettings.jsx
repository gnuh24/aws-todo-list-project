import { useEffect, useState, useRef } from "react";
import { https_user, https_authupdate, https_media } from "../../service/api";
import { Switch, Modal, Input, message, Button } from "antd";
import { BASE_URL } from "../../service/api"

export default function AccountSettings({
    onGotoChangePassword,
    onGotoChangeEmail,
    onGotoEnable2FA,
    onGotoDeleteAccount,
    refreshKey
}) {
    const dataUser = JSON.parse(localStorage.getItem("USER_INFO")) || {};
    const { id, displayName, email, avatar, twoFactorEnabled, receiveEmail } = dataUser;

    /* ================= STATE ================= */
    const [name, setName] = useState(displayName || "");
    const [tempName, setTempName] = useState(displayName || "");
    const [editing, setEditing] = useState(false);

    const [isNotificationEmail, setIsNotificationEmail] = useState(receiveEmail || false);
    const [isTwoFactorEnabled, setIsTwoFactorEnabled] = useState(twoFactorEnabled || false);

    const [disable2FAModalOpen, setDisable2FAModalOpen] = useState(false);
    const [otp, setOtp] = useState("");
    const [loadingDisable2FA, setLoadingDisable2FA] = useState(false);

    /* ===== Avatar TEMP ===== */
    const fileInputRef = useRef(null);
    const [tempAvatarFile, setTempAvatarFile] = useState(null);
    const [tempAvatarPreview, setTempAvatarPreview] = useState(null);

    /* ================= HANDLERS ================= */

    const handleCancel = () => {
        setTempName(name);
        setTempAvatarFile(null);
        setTempAvatarPreview(avatar || null);
        setEditing(false);
    };

    const handleChooseAvatar = () => {
        fileInputRef.current.click();
    };

    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;


        setTempAvatarFile(file);
        setTempAvatarPreview(URL.createObjectURL(file));
        setEditing(true);
    };

    const uploadAvatarIfNeeded = async () => {
        if (!tempAvatarFile) return avatar || null;

        const formData = new FormData();
        formData.append("file", tempAvatarFile);

        const res = await https_media.post(
            "/v1/local/uploads",
            formData,
            {
                headers: { "Content-Type": "multipart/form-data" }
            }
        );

        console.log(res);
        console.log(res.data);

        console.log(res.data.data);


        return res.data.data; // mediaId
    };

    const handleUpdate = async () => {
        try {
            setEditing(false);

            const uploadedAvatar = await uploadAvatarIfNeeded();

            const payload = {
                avatar: uploadedAvatar,
                displayName: tempName,
            };

            await https_user.patch("/v1/accounts/me", payload);

            setName(tempName);

            const updated = {
                ...dataUser,
                displayName: tempName,
                avatar: uploadedAvatar,
            };

            localStorage.setItem("USER_INFO", JSON.stringify(updated));

            message.success("Account updated successfully");
        } catch (error) {
            console.error(error);
            message.error("Update failed");
        }
    };

    const handleUpdateNotificationEmail = async (value) => {
        try {
            await https_user.patch("/v1/accounts/me", {
                receiveEmail: value,
            });
            setIsNotificationEmail(value);
        } catch (error) {
            message.error("Update failed");
        }
    };

    const handleUpdateTwoFactorEnabled = async (value) => {
        if (value) {
            onGotoEnable2FA();
        } else {
            setOtp("");
            setDisable2FAModalOpen(true);
        }
    };

    const handleDisable2FA = async () => {
        if (!otp || otp.length < 6) {
            message.error("Vui lòng nhập OTP hợp lệ");
            return;
        }

        try {
            setLoadingDisable2FA(true);

            await https_authupdate.post("/v1/2fa/disable", {
                otp: Number(otp),
            });

            message.success("Đã tắt xác thực 2 bước");

            setIsTwoFactorEnabled(false);

            const updated = {
                ...dataUser,
                twoFactorEnabled: false,
            };
            localStorage.setItem("USER_INFO", JSON.stringify(updated));

            setDisable2FAModalOpen(false);
        } catch (error) {
            message.error(error?.response?.data?.message || "OTP không hợp lệ");
        } finally {
            setLoadingDisable2FA(false);
        }
    };

    const fetchUser = async () => {
        try {
            const res = await https_user.get("/v1/accounts/me");
            setIsNotificationEmail(res.data.data.receiveEmail);
            setIsTwoFactorEnabled(res.data.data.twoFactorEnabled);
        } catch (e) {
            console.error(e);
        }
    };

    /* ================= EFFECT ================= */

    useEffect(() => {
        fetchUser();
    }, []);

    useEffect(() => {
        fetchUser();
    }, [refreshKey]);

    /* ================= RENDER ================= */

    return (
        <div className="text-gray-700">

            {/* HEADER */}
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-xl font-semibold">Account</h2>
            </div>

            {/* PHOTO */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-2">Photo</h3>

                <div className="flex items-center gap-5">
                    <img
                        src={
                            tempAvatarPreview
                                ? tempAvatarPreview
                                : avatar
                                    ? `${BASE_URL}/media/v1/local/${avatar}`
                                    : "https://i.pravatar.cc/80"
                        }
                        referrerPolicy="no-referrer"
                        className="w-20 h-20 rounded-full object-cover"
                    />


                    <div className="flex gap-3">
                        <button
                            onClick={handleChooseAvatar}
                            className="px-3 py-1 rounded border text-sm hover:bg-gray-100"
                        >
                            Change photo
                        </button>

                        {/* {tempAvatarPreview && (
                            <button
                                onClick={() => {
                                    setTempAvatarFile(null);
                                    setTempAvatarPreview(null);
                                    setEditing(true);
                                }}
                                className="px-3 py-1 rounded border border-red-400 text-red-500 text-sm hover:bg-red-50"
                            >
                                Remove photo
                            </button>
                        )} */}
                    </div>
                </div>

                <input
                    type="file"
                    accept="image/*"
                    ref={fileInputRef}
                    className="hidden"
                    onChange={handleAvatarChange}
                />

                <p className="text-xs text-gray-500 mt-2">
                    Pick a photo up to 45MB. Your avatar photo will be public.
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
                            disabled={
                                tempName.trim() === "" ||
                                (tempName === name && !tempAvatarFile)
                            }

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

                <Switch
                    checked={isTwoFactorEnabled}
                    onChange={handleUpdateTwoFactorEnabled}
                />
            </div>

            <Modal
                open={disable2FAModalOpen}
                title="Disable Two-Factor Authentication"
                onCancel={() => {
                    setDisable2FAModalOpen(false);
                    setIsTwoFactorEnabled(true);
                }}
                onOk={handleDisable2FA}
                confirmLoading={loadingDisable2FA}
                okText="Disable"
                cancelText="Cancel"
            >
                <Input
                    value={otp}
                    onChange={(e) => setOtp(e.target.value)}
                    placeholder="Enter 6-digit OTP"
                    maxLength={6}
                />
            </Modal>

            {/* Email Notifications */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-1">
                    Email notifications
                </h3>

                <Switch
                    checked={isNotificationEmail}
                    onChange={handleUpdateNotificationEmail}
                />
            </div>

            <Button danger onClick={onGotoDeleteAccount}>
                Xóa tài khoản
            </Button>

        </div>
    );
}
