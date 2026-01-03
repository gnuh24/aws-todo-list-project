// context/index.js
import {UIProvider} from "./UIContext";
import {ProjectProvider} from "./ProjectContext";
import {NotificationProvider} from "./NotificationContext";

export function AppProviders({ children }) {
    return (
        <UIProvider>
            <ProjectProvider>
                <NotificationProvider>
                    {children}
                </NotificationProvider>
            </ProjectProvider>
        </UIProvider>
    );
}
