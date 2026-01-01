import { useEffect, useState, useRef } from "react";
import { https_user, https_authupdate, https_media } from "../../service/api";
import { Switch, Modal, Input, message, Button } from "antd";
import AvatarCircle from "./AvatarCircle";

export default function AccountSettings({
    onGotoChangePassword,
    onGotoChangeEmail,
    onGotoEnable2FA,
    onGotoDeleteAccount,
    refreshKey
}) {
    const dataUser = JSON.parse(localStorage.getItem("USER_INFO")) || {};
    const { displayName, email, avatar, twoFactorEnabled, receiveEmail } = dataUser;

    /* ================= STATE ================= */
    const [name, setName] = useState(displayName || "");
    const [tempName, setTempName] = useState(displayName || "");
    const [editing, setEditing] = useState(false);

    const [isNotificationEmail, setIsNotificationEmail] = useState(receiveEmail || false);
    const [isTwoFactorEnabled, setIsTwoFactorEnabled] = useState(twoFactorEnabled || false);

    const [disable2FAModalOpen, setDisable2FAModalOpen] = useState(false);
    const [otp, setOtp] = useState("");
    const [recoveryKey, setRecoveryKey] = useState("");
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

        const res = await https_media.post("/v1/local/uploads", formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });

        return res.data.data;
    };

    const handleUpdate = async () => {
        try {
            setEditing(false);

            const uploadedAvatar = await uploadAvatarIfNeeded();

            const payload = {
                avatar: uploadedAvatar,
                displayName: tempName
            };

            await https_user.patch("/v1/accounts/me", payload);

            setName(tempName);

            const updated = {
                ...dataUser,
                displayName: tempName,
                avatar: uploadedAvatar
            };
            localStorage.setItem("USER_INFO", JSON.stringify(updated));

            message.success("Account updated successfully");
        } catch (error) {
            message.error("Update failed");
        }
    };

    const handleUpdateNotificationEmail = async (value) => {
        try {
            await https_user.patch("/v1/accounts/me", {
                receiveEmail: value
            });
            setIsNotificationEmail(value);
        } catch {
            message.error("Update failed");
        }
    };

    const handleUpdateTwoFactorEnabled = async (value) => {
        if (value) {
            onGotoEnable2FA();
        } else {
            setOtp("");
            setRecoveryKey("");
            setDisable2FAModalOpen(true);
        }
    };

    /* ================= DISABLE 2FA ================= */

    const handleDisable2FA = async () => {
        if (!otp && !recoveryKey) {
            message.error("Vui lòng nhập OTP hoặc Recovery Key");
            return;
        }

        if (otp && recoveryKey) {
            message.error("Chỉ được nhập OTP hoặc Recovery Key");
            return;
        }

        try {
            setLoadingDisable2FA(true);

            const payload = otp
                ? { otp: Number(otp) }
                : { recoveryKey: recoveryKey.trim() };

            await https_authupdate.post("/v1/2fa/disable", payload);

            message.success("Đã tắt xác thực 2 bước");

            setIsTwoFactorEnabled(false);

            const updated = {
                ...dataUser,
                twoFactorEnabled: false
            };
            localStorage.setItem("USER_INFO", JSON.stringify(updated));

            setDisable2FAModalOpen(false);
            setOtp("");
            setRecoveryKey("");
        } catch (error) {
            message.error(error?.response?.data?.message || "Xác thực thất bại");
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
            <h2 className="text-xl font-semibold mb-6">Account</h2>

            {/* PHOTO */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-2">Photo</h3>

                <div className="flex items-center gap-5">
                    {/* Avatar / Preview */}
                    {tempAvatarPreview ? (
                        <img
                            src={tempAvatarPreview}
                            alt="avatar preview"
                            className="w-20 h-20 rounded-full object-cover"
                        />
                    ) : (
                        <AvatarCircle
                            avatar={avatar}
                            name={displayName}
                            size={80}
                        />
                    )}

                    <button
                        onClick={handleChooseAvatar}
                        className="px-3 py-1 rounded border text-sm hover:bg-gray-100"
                    >
                        Change photo
                    </button>
                </div>

                <input
                    type="file"
                    accept="image/*"
                    ref={fileInputRef}
                    className="hidden"
                    onChange={handleAvatarChange}
                />
            </div>


            {/* NAME */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-1">Name</h3>

                <input
                    value={tempName}
                    onChange={(e) => setTempName(e.target.value)}
                    onFocus={() => setEditing(true)}
                    className="border px-3 py-2 rounded w-80 text-sm"
                />

                {editing && (
                    <div className="flex gap-2 mt-3">
                        <button onClick={handleCancel} className="border px-3 py-1 rounded">
                            Cancel
                        </button>
                        <button
                            onClick={handleUpdate}
                            className="bg-red-500 text-white px-3 py-1 rounded"
                        >
                            Update
                        </button>
                    </div>
                )}
            </div>

            {/* EMAIL */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-1">Email</h3>
                <p>{email}</p>
                <button onClick={onGotoChangeEmail} className="border px-3 py-1 rounded mt-2">
                    Change email
                </button>
            </div>

            {/* PASSWORD */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-1">Password</h3>
                <button onClick={onGotoChangePassword} className="border px-3 py-1 rounded">
                    Change password
                </button>
            </div>

            {/* 2FA */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-1">Two-factor authentication</h3>
                <Switch checked={isTwoFactorEnabled} onChange={handleUpdateTwoFactorEnabled} />
            </div>

            {/* DISABLE 2FA MODAL */}
            <Modal
                open={disable2FAModalOpen}
                title="Disable Two-Factor Authentication"
                onCancel={() => {
                    setDisable2FAModalOpen(false);
                    setIsTwoFactorEnabled(true);
                }}
                onOk={handleDisable2FA}
                confirmLoading={loadingDisable2FA}
            >
                <div className="space-y-4">
                    <Input
                        placeholder="Enter 6-digit OTP"
                        value={otp}
                        maxLength={6}
                        onChange={(e) => {
                            setOtp(e.target.value);
                            setRecoveryKey("");
                        }}
                    />

                    <div className="text-center text-xs text-gray-400">— OR —</div>

                    <Input
                        placeholder="Recovery Key (XXXX-XXXX-XXXX)"
                        value={recoveryKey}
                        onChange={(e) => {
                            setRecoveryKey(e.target.value.toUpperCase());
                            setOtp("");
                        }}
                    />
                </div>
            </Modal>

            {/* EMAIL NOTI */}
            <div className="mb-8">
                <h3 className="text-sm text-gray-500 mb-1">Email notifications</h3>
                <Switch checked={isNotificationEmail} onChange={handleUpdateNotificationEmail} />
            </div>

            <Button danger onClick={onGotoDeleteAccount}>
                Xóa tài khoản
            </Button>
        </div>
    );
}
