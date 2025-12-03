import { useState, useEffect } from "react";
import {https_user} from "../../service/api";

const notificationTypes = [
    { key: "PROJECT_MEMBER_ADDED", content: "You were added to the project" },
    { key: "PROJECT_MEMBER_ROLE_UPDATED", content: "A member's role was updated" },
    { key: "TASK_ASSIGNED", content: "You were assigned a new task" },
    { key: "TASK_COMMENTED", content: "A new comment was added to a task" },
    { key: "TASK_UPDATED", content: "A task has been updated" },
    { key: "TASK_COMPLETED", content: "A task has been completed" },
    { key: "TASK_DUE_SOON", content: "A task is due soon" },
    { key: "TASK_OVERDUE", content: "A task is overdue" },
    { key: "REQUEST_ACCEPTED", content: "You were accepted the invitation" },
    { key: "REQUEST_DECLINED", content: "Your were declined the invitation" },
    { key: "PROJECT_DELETED", content: "A project has been deleted" },
];

export function NotificationsSetting({ user }) {
    const [receiveEmail, setReceiveEmail] = useState(user.receiveEmail || false);
    const [originalReceiveEmail, setOriginalReceiveEmail] = useState(user.receiveEmail || false);
    const [notifications, setNotifications] = useState({});

    // Khởi tạo trạng thái notification theo key
    useEffect(() => {
        const init = {};
        notificationTypes.forEach(({ key }) => {
            init[key] = receiveEmail;
        });
        setNotifications(init);
    }, [receiveEmail]);

    const toggleReceiveEmail = () => setReceiveEmail((prev) => !prev);
    const cancelReceiveEmail = () => setReceiveEmail(originalReceiveEmail);
    const updateReceiveEmail = async () => {

        try{

            const res = await https_user.patch("accounts/me",{
                receiveEmail: receiveEmail,
            })

            if(res.status === 200) {
               alert(res.data.message);
            }

        } catch (e){
            console.log(e);
        }

        setOriginalReceiveEmail(receiveEmail)
    };

    const hasChanges = receiveEmail !== originalReceiveEmail;

    return (
        <div className="flex flex-col space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-black">Email Notifications</h3>
            </div>

            {/* Toggle Receive All */}
            <label className="flex items-center space-x-2 cursor-pointer">
                <span className="text-black"></span>
            </label>

            <button
                type="button"
                onClick={toggleReceiveEmail}
                className={`w-12 h-6 flex items-center rounded-full p-1 transition-colors duration-300 focus:outline-none ${
                    receiveEmail ? "bg-[#FF7875]" : "bg-gray-300"
                }`}
            >
                <div
                    className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform duration-300 ${
                        receiveEmail ? "translate-x-6" : "translate-x-0"
                    }`}
                />
            </button>

            {/* Update / Cancel Buttons */}
            <div
                className={`flex justify-end space-x-2 transition-all duration-300 overflow-hidden ${
                    hasChanges ? "max-h-16 opacity-100 translate-y-0" : "max-h-0 opacity-0 -translate-y-4"
                }`}
            >
                <button
                    onClick={updateReceiveEmail}
                    className="px-3 py-1 bg-[#FF7875] text-white rounded-md hover:bg-[#e06666]"
                >
                    Update
                </button>
                <button
                    onClick={cancelReceiveEmail}
                    className="px-3 py-1 bg-gray-200 rounded-md hover:bg-gray-300"
                >
                    Cancel
                </button>
            </div>

            {/* Notification List with Email & Mobile */}
            {/* Header row */}
            <div className="grid grid-cols-[80%_20%] font-semibold text-gray-700 px-2 py-1">
                <span>Notification</span>
                {/*<span className="text-center">Email</span>*/}
                {/*<span className="text-center">Mobile</span>*/}
            </div>

            <div className="flex flex-col max-h-[500px] border-t border-gray-200 pt-4">
                {/* Notification rows */}
                {notificationTypes.map(({ key, content }) => (
                    <div
                        key={key}
                        className="grid grid-cols-[80%_20%] items-center p-2 border border-gray-200 rounded-md hover:bg-gray-50 mt-1"
                    >
                        {/* Notification label */}
                        <span className="text-sm text-black">{content}</span>

                        {/*/!* Email checkbox *!/*/}
                        {/*<div className="flex justify-center">*/}
                        {/*    <input*/}
                        {/*        type="checkbox"*/}
                        {/*        checked={receiveEmail} // giữ trạng thái hiện tại*/}
                        {/*        onChange={(e) => e.preventDefault()} // ngăn checkbox thay đổi*/}
                        {/*        title="Function in progress" // hover sẽ hiện thông báo*/}
                        {/*        className="form-checkbox h-4 w-4 text-[#FF7875] accent-[#FF7875]"*/}
                        {/*    />*/}
                        {/*</div>*/}

                        {/*/!* Mobile checkbox *!/*/}
                        {/*<div className="flex justify-center">*/}
                        {/*    <input*/}
                        {/*        type="checkbox"*/}
                        {/*        checked={notifications[key]?.mobile || false}*/}
                        {/*        onChange={() =>*/}
                        {/*            setNotifications((prev) => ({*/}
                        {/*                ...prev,*/}
                        {/*                [key]: { ...prev[key], mobile: !prev[key]?.mobile },*/}
                        {/*            }))*/}
                        {/*        }*/}
                        {/*        className="form-checkbox h-4 w-4 text-[#36CFC9] accent-[#36CFC9]"*/}
                        {/*    />*/}
                        {/*</div>*/}
                    </div>
                ))}
            </div>



        </div>
    );
}
