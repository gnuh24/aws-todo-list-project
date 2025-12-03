const SettingsSidebar = ({ activeTab, setActiveTab }) => {
    return (
        <div className="w-64 p-4 bg-gray-50 border-r border-gray-200 h-full flex flex-col">

            {/*<div className="mb-4">*/}
            {/*    <input*/}
            {/*        type="text"*/}
            {/*        placeholder="Search"*/}
            {/*        className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"*/}
            {/*    />*/}
            {/*</div>*/}

            <nav className="flex-1 space-y-1">
                <button
                    onClick={() => setActiveTab("account")}
                    className={`w-full text-left px-3 py-2 rounded-md text-sm 
            ${activeTab === "account" ? "bg-gray-200 font-semibold" : "hover:bg-gray-100"}`}
                >
                    Account
                </button>

                <button
                    onClick={() => setActiveTab("notifications")}
                    className={`w-full text-left px-3 py-2 rounded-md text-sm 
            ${activeTab === "notifications" ? "bg-gray-200 font-semibold" : "hover:bg-gray-100"}`}
                >
                    Notifications
                </button>
            </nav>

            <div className="mt-auto pt-4 border-t border-gray-200">
                <button className="flex items-center w-full px-3 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-md">
                    <span className="mr-2">➕</span> Add team
                </button>
            </div>
        </div>
    );
};

export default SettingsSidebar;
