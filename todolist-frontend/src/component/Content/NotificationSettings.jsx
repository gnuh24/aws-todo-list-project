import { useEffect, useState } from "react";
import {Switch, Button, message, ConfigProvider} from "antd";
import { https_user } from "../../service/api";
import SpinnerLoading from "../Spinner/SpinnerLoading"; // chỉnh theo api của bạn

const theme = {
    token: {
        colorPrimary: "#EF4443",
        colorPrimaryHover: "#dc2626",
        colorPrimaryActive: "#b91c1c",
    },
};

export default function NotificationSettings() {
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [settings, setSettings] = useState([]);
    const [initialSettings, setInitialSettings] = useState([]);

    const hasChanges = JSON.stringify(settings) !== JSON.stringify(initialSettings); // So sánh nội dung chứ không so sánh ô nhớ

    useEffect(() => {
        fetchSettings();
    }, []);

    const fetchSettings = async () => {
        try {
            const res = await https_user.get("/v1/notification-settings");
            setSettings(res.data.data);
            setInitialSettings(res.data.data);
        } catch (err) {
            message.error("Không thể tải notification settings");
        } finally {
            setLoading(false);
        }
    };

    const updateSetting = (type, field, value) => {
        setSettings(prev =>
            prev.map(item =>
                item.notificationType === type
                    ? {...item, [field]: value}
                    : item
            )
        );
    };

    const handleCancel = () => {
        // reset về trạng thái ban đầu
        setSettings(JSON.parse(JSON.stringify(initialSettings)));
    };

    const handleSave = async () => {
        setSaving(true);
        try {
            await https_user.put("/v1/notification-settings/bulk", {
                settings: settings.map(s => ({
                    notificationType: s.notificationType,
                    enableWeb: s.enableWeb,
                    enableEmail: s.enableEmail,
                })),
            });

            setInitialSettings(JSON.parse(JSON.stringify(settings)));

            message.success("Cập nhật notification settings thành công");
        } catch (err) {
            message.error("Cập nhật thất bại");
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center h-full">
                <SpinnerLoading/>
            </div>
        );
    }

    return (
        <ConfigProvider theme={theme}>
            <div className="relative h-full overflow-hidden">
                {/* Scrollable content */}
                <div className="h-full overflow-y-auto px-6 pb-40">
                    <h1 className="text-xl font-semibold mb-6">
                        Notifications
                    </h1>

                    {/* Table header */}
                    <div className="grid grid-cols-3 text-sm text-gray-500 border-b pb-2 mb-2">
                        <div>Event</div>
                        <div className="text-center">Web</div>
                        <div className="text-center">Email</div>
                    </div>

                    {/* Settings list */}
                    <div className="space-y-3">
                        {settings.map(item => (
                            <div
                                key={item.notificationType}
                                className="grid grid-cols-3 items-center py-2"
                            >
                                <div className="text-sm font-medium">
                                    {formatNotificationLabel(
                                        item.notificationType
                                    )}
                                </div>

                                <div className="flex justify-center">
                                    <Switch
                                        checked={item.enableWeb}
                                        onChange={v =>
                                            updateSetting(
                                                item.notificationType,
                                                "enableWeb",
                                                v
                                            )
                                        }
                                    />
                                </div>

                                <div className="flex justify-center">
                                    <Switch
                                        checked={item.enableEmail}
                                        onChange={v =>
                                            updateSetting(
                                                item.notificationType,
                                                "enableEmail",
                                                v
                                            )
                                        }
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Floating Action Bar */}
                <div
                    className={`
                        absolute fixed bottom-0 left-1/2 -translate-x-1/2
                        w-[calc(100%-3rem)] max-w-4xl
                        z-50
                        bg-white border rounded-xl shadow-lg
                        px-8 py-4
                        transition-all duration-300 ease-out
                        ${
                        hasChanges
                            ? "opacity-100 translate-y-0"
                            : "opacity-0 translate-y-4 pointer-events-none"
                    }
                    `}
                >
                    <div className="flex justify-end gap-3">
                        <Button onClick={handleCancel}>
                            Cancel
                        </Button>

                        <Button
                            type="primary"
                            loading={saving}
                            onClick={handleSave}
                        >
                            Save changes
                        </Button>
                    </div>
                </div>
            </div>
        </ConfigProvider>
    );
}
/* =========================
   Utils
   ========================= */

function formatNotificationLabel(type) {
    return type
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/(^\w|\s\w)/g, m => m.toUpperCase());
}
