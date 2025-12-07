import { useState, useEffect } from "react";
import { useAppContext } from "../../layout/MainLayoutNoti";
import { CheckOutlined } from "@ant-design/icons";
import {https_auth, https_notification, https_taskflow} from "../../service/api";
import { useNavigate } from "react-router-dom";
import Spinner from "../../component/Spinner/Spinner";
import NotificationItem from "../../component/Notification/NotificationItem";


export default function NotificationsPage() {
    const { notifications, setNotifications, setCountNotificationsUnRead, countNotificationsUnRead, totalPagesNotification, setTotalPagesNotification, pageSizeNotification, pageNumberNotification, setPageNumberNotification } = useAppContext();
    const [tab, setTab] = useState("All"); // default là Unread
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const getAllNotifications = async (pageNumber = 1) => {
            setLoading(true);
            try {
                const response = await https_notification.get("/v1/notifications/my-notification", {
                    params: {
                        pageNumber: pageNumber,
                        pageSize: pageSizeNotification,
                        isRead: tab === "Unread" ? false : null, // call API với điều kiện
                    }
                });

                const { content, totalPages } = response.data.data;

                console.log(content);

                // Dựa vào pageNumber, không dựa vào biến page trong scope
                setNotifications((prev) =>
                    pageNumber === 1 ? content : [...prev, ...content]
                );
                setTotalPagesNotification(totalPages);
            } catch (error) {
                console.error("Fetch notifications failed:", error);
            } finally {
                setLoading(false);
            }
        };
        getAllNotifications(pageNumberNotification);
    }, [pageNumberNotification, tab]);


    const UpdateListNotification = (status, idNotification) => {
        if (status) {
            setCountNotificationsUnRead((prev) => prev - 1)
        }else{
            setCountNotificationsUnRead((prev) => prev + 1)
        }

        // Nếu là tab Unread thì xóa thông báo khỏi đó

        if (tab === "Unread"){
            setNotifications((prev) => prev.filter((item) => item.id !== idNotification));
        }else{
            setNotifications((prev) =>
                prev.map((item) =>
                    item.id === idNotification ? { ...item, read: !item.read } : item
                )
            );

        }
    }

    const handleUpdateStatus =  async (status, idNotification) => {
        try{
            await https_notification.patch(`/v1/notifications/${idNotification}/read-status`, {
                isRead: status,
            })

            UpdateListNotification(status, idNotification);

        }
        catch(error) {
            console.error("Fetch notifications failed:", error);
        }
    }

    const handleClickOnNotification = async (idProject, idNotification, isRead) => {
        try {
            // Gọi API — nếu 200 thì res.data là project
            const res = await https_taskflow.get(`/v1/projects/${idProject}`);
            const project = res.data.data; // ← trực tiếp là dữ liệu project

            if (!project) {
                alert("Không tìm thấy project")
                return;
            }

            // Cập nhật trạng thái đã đọc nếu cần
            if (!isRead) {
                await handleUpdateStatus(true, idNotification);
            }

            // Điều hướng
            navigate(`/app/projects/${encodeURIComponent(project.name)}/${idProject}`);
        } catch (error) {
            console.error("Fetch project failed:", error);

            // Lấy message từ error.response.data nếu có
            const errorMessage =
                error.response?.data?.message ||
                "An error occurred while fetching the project.";

            alert(errorMessage);
        }
    };

    // Cập nhật đồng loạt thông báo
    const markAllRead = async () => {
        try {
            const unreadIds = notifications.filter(n => !n.read).map(n => n.id);
            if (unreadIds.length === 0) return;

            // Call API cho từng id
            const res = await https_notification.patch(`/v1/notifications/read-status/bulk`, {
                ids: unreadIds,
                isRead: true,
            })

            console.log(res)

            // Update state local
            const updated = notifications.map(n => ({ ...n, read: true }));
            if (tab === "All") {
                setNotifications(updated);
            }else{
                setNotifications([]);
            }

            setCountNotificationsUnRead((prev) => prev - unreadIds.length);

        } catch (err) {
            console.error("Mark all notifications as read failed:", err);
        }
    };


    return (
        <div className="w-full mx-auto">
            <div className="flex justify-end items-center">
                <button
                    className="text-gray-500 text-base hover:underline flex items-center pt-2 pr-2"
                    onClick={markAllRead}
                >
                    <CheckOutlined className="mr-2" />
                    Mark all as read
                </button>

            </div>


            <div className="w-full max-w-3xl mx-auto">
                <h1 className="text-xl font-bold">Notifications</h1>
                {/* Tabs */}
                <div className="inline-flex gap-2 mb-4 rounded-full bg-gray-200 px-2 py-2 mt-5">
                    <button
                        onClick={() => {
                            setTab("All")
                            setPageNumberNotification(1)
                        }}
                        className={`px-3 py-1 rounded-full ${
                            tab === "All" ? "bg-white font-bold" : "bg-gray-200 hover:bg-gray-200"
                        }`}
                    >
                        All
                    </button>
                    <button
                        onClick={() => {
                            setTab("Unread")
                            setPageNumberNotification(1)
                        }}
                        className={`px-3 py-1 rounded-full ${
                            tab === "Unread" ? "bg-white font-bold" : "bg-gray-200 hover:bg-gray-200"
                        }`}
                    >
                        Unread {countNotificationsUnRead}
                    </button>
                </div>

                {/* Notification list */}
                <div className="flex flex-col h-[70vh] overflow-y-auto">
                <div className="flex flex-col gap-2 flex-1">
                    {notifications.map((n) => (
                        <div key={n.id}>
                            <NotificationItem  notification={n} handleClickOnNotification={handleClickOnNotification} handleUpdateStatus={handleUpdateStatus} UpdateListNotification={UpdateListNotification} />
                        </div>
                    ))}


                    {notifications.length <= 0  && (
                        <div className="flex flex-col items-center justify-center py-10 text-gray-500">
                            <img
                                src="https://todoist.b-cdn.net/assets/images/75a6f35ba08afa86.png"
                                alt="No notifications"
                                className="w-48 h-48 mb-4 opacity-80"
                                onError={(e) => (e.currentTarget.style.display = 'none')}
                            />
                            <p className="text-lg font-medium">No notifications yet</p>
                            <p className="text-sm text-gray-400">You’ll see updates and alerts here.</p>
                        </div>
                    )}
                </div>

                </div>

                {pageNumberNotification < totalPagesNotification && (
                    <div className="flex justify-center py-4 sticky bottom-0 bg-white border-t border-gray-200">
                        <button
                            onClick={() => setPageNumberNotification((prev) => prev + 1)}
                            disabled={loading}
                            className="px-4 py-2 bg-gray-800 text-white rounded-md hover:bg-[#FEF2F2] hover:text-[#DC2626] transition-colors duration-200 disabled:opacity-50"
                        >
                            {loading ? "Loading..." : "Load more"}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
