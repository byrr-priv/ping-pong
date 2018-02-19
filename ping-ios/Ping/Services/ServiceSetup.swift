//
//  Copyright © 2018 Frallware. All rights reserved.
//

import Foundation

class AppServices {

    private var services: [String : Any] = [:]

    init() {
        setupAllServices()
    }

    /// MARK: - Services setup

    private func setupAllServices() {
        let api = _PingAPI(baseURL: URL(string: "http://localhost:8080")!)
        add(service: api, forType: PingAPI.self)
    }

    private func add<T>(service: Any, forType type: T.Type) {
        services[String(describing: type)] = service
    }
}

extension AppServices: ServiceContainer {

    func service<T>(ofType type: T.Type) -> Any {
        return services[String(describing: type)]!
    }
}
