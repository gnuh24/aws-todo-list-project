import Sidebar from "../component/Sidebar/Sidebar";
import { useEffect } from "react";
import { https_notification } from "../service/api";
import { Outlet } from "react-router-dom";
import ModalNotification from "../component/Notification/ModalNotification";
import SettingsModal from "../component/Modal/SettingsModal";
import { WebSocketClient } from "../websocket/WebSocketClient";
import { AppProviders } from "../context";

import { useNotificationContext } from "../context/NotificationContext";
import { useUIContext } from "../context/UIContext";

/* ================= CONTENT ================= */
function MainLayoutContent() {
    const {
        setCountNotificationsUnRead,
        newNotificationFromWebsocket,
        setNewNotificationFromWebsocket
    } = useNotificationContext();

    const {
        isSettingsOpen,
        setIsSettingsOpen,
    } = useUIContext();

    /* ===== LOAD COUNT UNREAD ===== */
    useEffect(() => {
        const getCountNotifications = async () => {
            const res = await https_notification.get(
                "/v1/notifications/count-my-notification-unread",
                {
                    params: { isRead: false },
                }
            );
            setCountNotificationsUnRead(res.data.data);
        };

        getCountNotifications();
    }, []);

    return (
        <>
            <div className="ml-72 flex-1 flex flex-col">
                <Sidebar />

                <div className="flex-1 bg-white">
                    <WebSocketClient />
                    <Outlet />
                </div>

                {newNotificationFromWebsocket !== null && <ModalNotification message={newNotificationFromWebsocket} setNewNotificationFromWebsocket={setNewNotificationFromWebsocket} />}
            </div>

            {isSettingsOpen && (
                <SettingsModal onClose={() => setIsSettingsOpen(false)} />
            )}
        </>
    );
}

/* ================= EXPORT ================= */
export default function MainLayout() {
    return (
        <AppProviders>
            <MainLayoutContent />
        </AppProviders>
    );
}
