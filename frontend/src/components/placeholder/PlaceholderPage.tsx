type PlaceholderPageProps = {
    kicker: string;
    title: string;
    message: string;
};

export function PlaceholderPage({ kicker, title, message }: PlaceholderPageProps) {
    return (
        <section className="placeholder-page">
            <p className="placeholder-kicker">{kicker}</p>
            <h1 className="placeholder-title">{title}</h1>
            <p className="placeholder-message">{message}</p>
        </section>
    );
}
