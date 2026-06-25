type HostelLogoProps = {
    className?: string;
};

export function HostelLogo({ className }: HostelLogoProps) {
    return (
        <svg
            className={className}
            viewBox="0 0 64 64"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            aria-hidden="true"
            focusable="false"
        >
            <path
                d="M12 29L32 14L52 29"
                stroke="currentColor"
                strokeWidth="4"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path d="M18 28H46V52H18V28Z" fill="currentColor" opacity="0.16" />
            <path
                d="M18 28V52H46V28"
                stroke="currentColor"
                strokeWidth="4"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="M27 52V40H37V52"
                stroke="currentColor"
                strokeWidth="4"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path d="M23 34H29V40H23V34Z" fill="currentColor" />
            <path d="M35 34H41V40H35V34Z" fill="currentColor" />
            <path
                d="M20 24H44"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                opacity="0.72"
            />
        </svg>
    );
}
