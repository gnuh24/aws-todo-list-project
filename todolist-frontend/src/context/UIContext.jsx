import { createContext, useContext, useState } from "react";

const UIContext = createContext();

export const useUIContext = () => useContext(UIContext);

export function UIProvider({ children }) {
    const [isSettingsOpen, setIsSettingsOpen] = useState(false);

    return (
        <UIContext.Provider value={{ isSettingsOpen, setIsSettingsOpen }}>
            {children}
        </UIContext.Provider>
    );
}
