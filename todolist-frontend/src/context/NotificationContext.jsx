import { createContext, useContext, useState } from "react";

const NotificationContext = createContext();

const PAGE_SIZE = 5;

export const useNotificationContext = () =>
    useContext(NotificationContext);

export function NotificationProvider({ children }) {
    const [countNotificationsUnRead, setCountNotificationsUnRead] = useState(0);
    const [notifications, setNotifications] = useState([]);
    const [pageSizeNotification] = useState(PAGE_SIZE);
    const [pageNumberNotification, setPageNumberNotification] = useState(1); // trang hiện tại const [totalPagesNotification, setTotalPagesNotification] = useState(0); const [pageNotification, setPageNotification] = useState(1); // trang hiện tại
    const [newNotificationFromWebsocket, setNewNotificationFromWebsocket] = useState(null);

    return (
        <NotificationContext.Provider
            value={{
                notifications,
                setNotifications,
                countNotificationsUnRead, setCountNotificationsUnRead,
                pageSizeNotification,
                pageNumberNotification, setPageNumberNotification,
                newNotificationFromWebsocket, setNewNotificationFromWebsocket
            }}
        >
            {children}
        </NotificationContext.Provider>
    );
}
