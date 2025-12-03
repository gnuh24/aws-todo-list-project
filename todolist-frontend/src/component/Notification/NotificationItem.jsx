import {https_taskflow} from "../../service/api";

export default function NotificationItem({ notification, handleClickOnNotification, handleUpdateStatus }) {

    const typeNotificationAddMember = "PROJECT_MEMBER_ADDED"
    const statusForReponse = {
        accepted: "ACCEPTED",
        declined: "DECLINED",
    }

    const idUserLogging = JSON.parse(localStorage.getItem("USER_INFO"))?.id

    const handleAcceptInvite = async (notification) => {
        try{
            const res = await https_taskflow.patch(`/v1/projects/${notification.projectId}/members/response`, {
                status: statusForReponse.accepted,
            })

           if (res.status === 200) {
               await handleUpdateStatus(!notification.read, notification.id)
           }
        }
        catch(error) {
            console.error("Fetch notifications failed:", error);
        }
    }

    const handleRejectInvite = async (notification) => {
        try{
            const res = await https_taskflow.patch(`/v1/projects/${notification.projectId}/members/response`, {
                status: statusForReponse.declined,
            })

            if (res.status === 200) {
                await handleUpdateStatus(!notification.read, notification.id)
            }
        }
        catch(error) {
            console.error("Fetch notifications failed:", error);
        }
    }

    return (
        <>
            <div className={`relative rounded-lg transition-all border-t border-b border-gray-300 ${
                notification.read
                    ? "bg-white hover:bg-gray-100"
                    : "bg-red-50 border-l-4 border-red-600 hover:bg-red-30"
            }`}>
                {/* Khối thông báo */}
                <div
                    className={`flex items-start gap-3 p-3  cursor-pointer mr-5 `}
                    onClick={() => handleClickOnNotification(notification.projectId, notification.id, notification.read)}
                >
                    {/* Avatar */}
                    <div className="w-9 h-9 rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold">
                        {notification.avatar ? (
                            <img
                                src={notification.avatar}
                                alt="avatar"
                                className="w-full h-full object-cover rounded-full"
                                onError={(e) => (e.currentTarget.src = "/default-avatar.png")}
                            />
                        ) : (
                            notification?.displayName?.[0]?.toUpperCase() || "U"
                        )}
                    </div>

                    {/* Content */}
                    <div className="flex-1">
                        <div className="flex justify-between items-start">
                            {notification.type === "TASK_DUE_SOON" || notification.type === "TASK_OVERDUE" ? (
                                <span className="font-base text-gray-800">
                                    {notification.title}
                                </span>
                                ) : (
                                <span className="font-base text-gray-800">
                                    {notification.title} bởi <b>{notification.displayName}</b>
                                </span>
                            )}

                        </div>

                        <p className="text-sm text-gray-700 mt-1">{notification.content}</p>

                        <span className="text-xs text-gray-400">
                                      {new Date(notification.createdAt).toLocaleString()}
                                    </span>
                    </div>

                    {/* Read status dot */}
                    {!notification.read && (
                        <span className="w-3 h-3 bg-[#F48318] rounded-full absolute top-3 right-2"></span>
                    )}
                </div>

                {/* ---- Nút Accept / Denied ---- */}
                {notification.type === typeNotificationAddMember && !notification.read &&(
                    <div className="flex gap-3 my-3 ml-3">
                        <button
                            className="px-3 py-1 bg-green-500 text-white rounded-md text-sm hover:bg-green-600"
                            onClick={(e) => {
                                e.stopPropagation();
                                handleAcceptInvite(notification);
                            }}
                        >
                            Accept
                        </button>

                        <button
                            className="px-3 py-1 bg-red-500 text-white rounded-md text-sm hover:bg-red-600"
                            onClick={(e) => {
                                e.stopPropagation();
                                handleRejectInvite(notification);
                            }}
                        >
                            Denied
                        </button>
                    </div>
                )}

                {/* Checkbox riêng, không nằm trong div có onClick */}
                {notification.type !== typeNotificationAddMember && (<div className="absolute bottom-2 right-2">
                    <input
                        type="checkbox"
                        checked={notification.read}
                        title={notification.read ? "Mark as unread" : "Mark as read"}
                        onChange={() => {
                            handleUpdateStatus(!notification.read, notification.id);
                        }}
                        className="
                                      w-3 h-3
                                      appearance-none
                                      rounded-full
                                      border border-gray-600
                                      checked:bg-[#000000]
                                      checked:border-[#000000]
                                      focus:outline-none
                                      cursor-pointer
                                    "
                    />
                </div>)}
            </div>




        </>
    );
}
