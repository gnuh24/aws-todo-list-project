
import SettingsSidebar from "../Sidebar/SettingsSidebar";
import {useEffect, useState} from "react";
import {AccountSetting} from "../Settings/AccountSetting";
import {NotificationsSetting} from "../Settings/NotificationsSetting";
import {https_user} from "../../service/api";
import SpinnerForSettings from "../Spinner/SpinnerForSettings";


const SettingsModal = ({ onClose }) => {
    const [activeTab, setActiveTab] = useState("account");

    const [user, setUser] = useState(null);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await https_user.get("accounts/me"); // phải await
                console.log(res.data.data);
                setUser(res.data.data);
            } catch (e) {
                console.error(e);
            }
        };

        fetchUser();
    }, []);

    const renderContent = () => {
        if (!user) return <SpinnerForSettings />;
        switch (activeTab) {
            case "account":
                return <AccountSetting user={user}/>;
            case "notifications":
                return <NotificationsSetting user={user}/>;
            default:
                return <AccountSetting />;
        }
    };

    return (

        <div
            className="fixed inset-0 bg-black/40 flex justify-center items-center z-50"
            onClick={onClose} // click ngoài để close
        >
            <div
                className="bg-white rounded-lg shadow-xl w-[1000px] h-[800px] flex flex-col overflow-hidden"
                onClick={(e) => e.stopPropagation()} // chặn đóng khi click trong modal
            >
                {/* Header */}
                <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200 bg-gray-50">
                    <h2 className="text-lg font-semibold">Settings</h2>
                    <button
                        className="text-gray-500 hover:text-black text-xl"
                        onClick={onClose}
                    >
                        ✕
                    </button>
                </div>

                {/* Body */}
                <div className="flex flex-1 overflow-hidden">
                    {/* Sidebar */}
                    <SettingsSidebar
                        activeTab={activeTab}
                        setActiveTab={setActiveTab}
                    />

                    {/* Content */}
                    <div className="flex-1 p-6 overflow-y-auto">
                        {renderContent()}
                    </div>
                </div>
            </div>


        </div>
    );
};

export default SettingsModal;