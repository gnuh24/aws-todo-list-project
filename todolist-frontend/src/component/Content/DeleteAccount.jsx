import { useState, useEffect } from "react";
import { Input, Button, message } from "antd";
import { https_authupdate } from "../../service/api";

export default function DeleteAccount({ onBack }) {
    const [form, setForm] = useState({
        email: "",
        password: "",
        confirmText: "",
        otp: "",
        totp: ""
    });

    const [is2FAEnabled, setIs2FAEnabled] = useState(false);
    const [loading, setLoading] = useState(false);
    const [sendingOtp, setSendingOtp] = useState(false);

    useEffect(() => {

        const dataUser = JSON.parse(localStorage.getItem("USER_INFO")) || {};
        const { twoFactorEnabled } = dataUser;

        const flag = twoFactorEnabled
        setIs2FAEnabled(flag === true);

    }, []);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    /* =====================================================
     * Send OTP Email (only when 2FA OFF)
     * ===================================================== */
    const handleSendOtp = async () => {
        if (!form.email) {
            return message.warning("Vui lòng nhập email trước");
        }

        try {
            setSendingOtp(true);
            await https_authupdate.post("/v1/send-delete-account-otp", {
                email: form.email
            });
            message.success("Mã OTP đã được gửi qua email");
        } catch (err) {
            message.error(
                err.response?.data?.message || "Gửi OTP thất bại"
            );
        } finally {
            setSendingOtp(false);
        }
    };

    /* =====================================================
     * Delete account
     * ===================================================== */
    const handleDelete = async () => {
        try {
            setLoading(true);

            const payload = {
                email: form.email,
                password: form.password,
                confirmText: form.confirmText,
                ...(is2FAEnabled
                    ? { totp: parseInt(form.totp) }
                    : { otp: form.otp })
            };


            await https_authupdate.delete("/v1/delete-account", {
                data: payload
            });

            message.success("Tài khoản đã được xóa");
            localStorage.clear();
            window.location.href = "/login";

        } catch (err) {
            message.error(
                err.response?.data?.message || "Xóa tài khoản thất bại"
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-xl">
            <h2 className="text-xl font-semibold text-red-600 mb-6">
                Xóa tài khoản
            </h2>

            <Input
                name="email"
                placeholder="Email hiện tại"
                className="mb-3"
                onChange={handleChange}
            />

            <Input.Password
                name="password"
                placeholder="Mật khẩu"
                className="mb-3"
                onChange={handleChange}
            />

            {is2FAEnabled ? (
                <Input
                    name="totp"
                    placeholder="Mã 2FA (Google Authenticator)"
                    className="mb-3"
                    onChange={handleChange}
                />
            ) : (
                <>
                    <div className="flex gap-2 mb-3">
                        <Input
                            name="otp"
                            placeholder="Mã OTP gửi qua Email"
                            onChange={handleChange}
                        />
                        <Button
                            onClick={handleSendOtp}
                            loading={sendingOtp}
                        >
                            Gửi OTP
                        </Button>
                    </div>
                </>
            )}

            <Input
                name="confirmText"
                placeholder="Nhập 'delete' để xác nhận"
                className="mb-6"
                onChange={handleChange}
            />

            <div className="flex gap-3">
                <Button onClick={onBack}>
                    Hủy
                </Button>

                <Button
                    danger
                    type="primary"
                    loading={loading}
                    onClick={handleDelete}
                >
                    Xóa tài khoản
                </Button>
            </div>
        </div>
    );
}
