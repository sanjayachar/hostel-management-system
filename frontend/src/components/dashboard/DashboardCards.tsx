import { Link } from "react-router-dom";
import { DashboardIcon, type DashboardIconName } from "./DashboardIcon";

export type DashboardCardItem = {
    title: string;
    description: string;
    to: string;
    icon: DashboardIconName;
};

type DashboardCardsProps = {
    items: DashboardCardItem[];
};

export function DashboardCards({ items }: DashboardCardsProps) {
    return (
        <div className="dashboard-card-grid">
            {items.map((item) => (
                <Link className="dashboard-card" to={item.to} key={item.to}>
                    <div className="dashboard-card-visual">
                        <DashboardIcon name={item.icon} className="dashboard-card-hero-icon" />
                    </div>
                    <div className="dashboard-card-footer">
                        <span className="dashboard-card-icon-box">
                            <DashboardIcon name={item.icon} className="dashboard-card-small-icon" />
                        </span>
                        <span>
                            <span className="dashboard-card-title">{item.title}</span>
                            <span className="dashboard-card-description">{item.description}</span>
                        </span>
                    </div>
                </Link>
            ))}
        </div>
    );
}
