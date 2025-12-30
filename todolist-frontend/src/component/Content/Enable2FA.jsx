import { ArrowLeftOutlined, CopyOutlined, DownloadOutlined } from "@ant-design/icons";
import { Input, message, Spin } from "antd";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { https_authupdate } from "../../service/api";

export default function Enable2FA({ onBack }) {
    const [qrUrl, setQrUrl] = useState("");
    const [secret, setSecret] = useState("");
    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);

    const [recoveryKeys, setRecoveryKeys] = useState([]);
    const [verified, setVerified] = useState(false);

    // ===============================
    // Step 1: Setup 2FA
    // ===============================
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

    // ===============================
    // Step 2: Verify OTP
    // ===============================
    const handleVerifyOtp = async () => {
        if (!otp) return toast.error("Please enter OTP.");

        try {
            const res = await https_authupdate.post("/v1/2fa/verify", {
                otp: parseInt(otp),
            });

            // Update local storage
            const dataUser = JSON.parse(localStorage.getItem("USER_INFO")) || {};
            localStorage.setItem(
                "USER_INFO",
                JSON.stringify({ ...dataUser, twoFactorEnabled: true })
            );

            setRecoveryKeys(res.data.data.recoveryKeys || []);
            setVerified(true);

            message.success("2FA enabled successfully!");
        } catch (err) {
            console.error(err);
            toast.error("Invalid OTP.");
        }
    };

    // ===============================
    // Copy recovery keys
    // ===============================
    const handleCopy = () => {
        navigator.clipboard.writeText(recoveryKeys.join("\n"));
        message.success("Recovery keys copied!");
    };

    // ===============================
    // Download recovery keys
    // ===============================
    const handleDownload = () => {
        const content = `
SGU TODOLIST - RECOVERY KEYS
===========================

${recoveryKeys.join("\n")}

⚠️ Each recovery key can be used only once.
Keep this file in a safe place.
`;

        const blob = new Blob([content], { type: "text/plain;charset=utf-8" });
        const url = URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = "sgu-todolist-recovery-key.txt";
        a.click();

        URL.revokeObjectURL(url);
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

            {loading && <Spin />}

            {/* ===============================
                BEFORE VERIFY
            =============================== */}
            {!verified && (
                <>
                    <p className="text-sm mb-6">
                        Scan the QR code using Google Authenticator or similar apps.
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

                    {/* Buttons */}
                    <div className="flex justify-end gap-3 pr-3 mt-8">
                        <button
                            onClick={onBack}
                            className="px-4 py-1.5 rounded border text-sm hover:bg-gray-100"
                        >
                            Cancel
                        </button>

                        <button
                            onClick={handleVerifyOtp}
                            className="px-4 py-1.5 rounded bg-[#f8b4a0] text-white text-sm hover:bg-[#f7a58d]"
                        >
                            Enable 2FA
                        </button>
                    </div>
                </>
            )}

            {/* ===============================
                AFTER VERIFY
            =============================== */}
            {verified && (
                <>
                    <h3 className="text-lg font-semibold mb-2 text-green-600">
                        Recovery Keys
                    </h3>

                    <p className="text-sm mb-4 text-gray-600">
                        Store these recovery keys in a safe place. Each key can be used only once.
                    </p>

                    <div className="bg-gray-100 p-4 rounded w-fit">
                        {recoveryKeys.map((key) => (
                            <div key={key} className="font-mono text-sm">
                                {key}
                            </div>
                        ))}
                    </div>

                    <div className="flex gap-3 mt-4">
                        <button
                            onClick={handleCopy}
                            className="flex items-center gap-1 px-3 py-1.5 border rounded text-sm hover:bg-gray-100"
                        >
                            <CopyOutlined /> Copy
                        </button>

                        <button
                            onClick={handleDownload}
                            className="flex items-center gap-1 px-3 py-1.5 border rounded text-sm hover:bg-gray-100"
                        >
                            <DownloadOutlined /> Download
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}
