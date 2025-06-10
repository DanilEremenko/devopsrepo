module.exports = {
    transform: {
        "^.+\\.[jt]sx?$": "babel-jest",
    },
    testEnvironment: "jsdom",
    moduleNameMapper: {
        "\\.(css|less|scss|sass)$": "identity-obj-proxy",
        "\\.(svg|png|jpg|jpeg|gif)$": "<rootDir>/__mocks__/fileMock.js",
    },
    moduleDirectories: ["node_modules", "src"],
    transformIgnorePatterns: [
        "/node_modules/(?!(axios|react-router-dom|react-router)/)"
    ],
    extensionsToTreatAsEsm: [".jsx"],
};
