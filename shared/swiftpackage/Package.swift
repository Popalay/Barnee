// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "BarneeShared",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "BarneeShared",
            targets: ["BarneeShared"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "BarneeShared",
            path: "./BarneeShared.xcframework"
        ),
    ]
)
