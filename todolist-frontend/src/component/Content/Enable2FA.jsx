import { ArrowLeftOutlined } from "@ant-design/icons";
import { Input, message } from "antd";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { https_authupdate } from "../../service/api";

export default function Enable2FA({ onBack }) {
    const [qrUrl, setQrUrl] = useState("");
    const [secret, setSecret] = useState("");
    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);

    // Step 1: setup 2FA
    useEffect(() => {
        const setup2FA = async () => {
            try {
                setLoading(true);
                const res = await https_authupdate.post("/v1/2fa/setup");

                setQrUrl(res.data.data.qrUrl);
                setSecret(res.data.data.secret);
            } catch (err) {
                console.error(err);
                toast.error("Failed to setup 2FA.");
            } finally {
                setLoading(false);
            }
        };

        setup2FA();
    }, []);

    // Step 2: verify OTP
    const handleVerifyOtp = async () => {
        if (!otp) return toast.error("Please enter OTP.");

        try {
            await https_authupdate.post("/v1/2fa/verify", { otp: otp });

            message.success("2FA enabled successfully!");
            onBack();
        } catch (err) {
            console.error(err);
            toast.error("Invalid OTP.");
        }
    };

    return (
        <div className="text-gray-700">
            {/* Header */}
            <div className="flex items-center gap-3 mb-8">
                <button onClick={onBack} className="p-1 rounded hover:bg-gray-200">
                    <ArrowLeftOutlined />
                </button>
                <h2 className="text-xl font-semibold">Enable 2FA</h2>
            </div>

            {/* Description */}
            <p className="text-sm mb-8">
                Protect your account by enabling two-factor authentication (2FA).
                Scan the QR code below using Google Authenticator or similar apps.
            </p>

            {/* QR Code */}
            {qrUrl && (
                <div className="mb-6">
                    <img src={qrUrl} alt="QR Code" className="border rounded" />
                </div>
            )}

            {/* Secret */}
            {secret && (
                <div className="mb-6">
                    <h3 className="text-sm font-medium mb-1">Secret key</h3>
                    <Input value={secret} readOnly className="w-80" />
                    <p className="text-xs text-gray-500 mt-1">
                        Save this secret in case you lose access to your authenticator app.
                    </p>
                </div>
            )}

            {/* OTP */}
            <div className="mb-6">
                <h3 className="text-sm font-medium mb-1">Authentication code</h3>
                <Input
                    value={otp}
                    onChange={(e) => setOtp(e.target.value)}
                    placeholder="Enter 6-digit OTP"
                    maxLength={6}
                    className="w-80"
                />
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
                    onClick={handleVerifyOtp}
                    disabled={loading}
                    className="px-4 py-1.5 rounded bg-[#f8b4a0] text-white text-sm hover:bg-[#f7a58d]"
                >
                    Enable 2FA
                </button>
            </div>
        </div>
    );
}
