import Sidebar from "../component/Sidebar/Sidebar";
import {createContext, useContext, useEffect, useState} from "react";
import {Outlet} from "react-router-dom";
import {https_notification} from "../service/api";
import NotificationSocket from "../component/Notification/NotificationSocket";
import MedalNotification from "../component/Notification/MedalNotification";

const NotificationContext = createContext();

export const useNotifications = () => useContext(NotificationContext);

const PAGE_SIZE = 5;

export default function MainLayout() {

    const [countNotificationsUnRead, setCountNotificationsUnRead] = useState(0);

    const [notifications, setNotifications] = useState([]);

    const [pageSizeNotification] = useState(PAGE_SIZE);

    const [pageNumberNotification, setPageNumberNotification] = useState(1);        // trang hiện tại

    const [totalPagesNotification, setTotalPagesNotification] = useState(0);

    const [pageNotification, setPageNotification] = useState(1);        // trang hiện tại

    const [newNotificationFormWebsocket, setNewNotificationFormWebsocket] = useState(null);

    useEffect(() => {
        const getCountNotifications = async () => {
            const res = await https_notification.get("/v1/notifications/count-my-notification-unread",{
                params: {
                    isRead: false,
                }
            });

            setCountNotificationsUnRead(res.data.data);
        }

        getCountNotifications();
    },[]);

    return (
        <NotificationContext.Provider value={{ countNotificationsUnRead, setCountNotificationsUnRead, notifications, setNotifications, pageSizeNotification, totalPagesNotification, setTotalPagesNotification, pageNotification, setPageNotification, setNewNotificationFormWebsocket, pageNumberNotification, setPageNumberNotification }}>
            <div className="ml-72 flex-1 flex flex-col">
                <Sidebar />
                <div className="flex-1 bg-white">
                    <Outlet/>
                </div>
                <NotificationSocket/>
                {newNotificationFormWebsocket !== null && <MedalNotification message={newNotificationFormWebsocket} setNewNotificationFormWebsocket={setNewNotificationFormWebsocket} />}
            </div>
        </NotificationContext.Provider>
    );
}
