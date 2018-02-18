//
//  Copyright © 2018 Frallware. All rights reserved.
//

import Foundation

enum Ping {}

extension Ping {

    struct Response: Decodable {
        let services: [Service]
    }

    struct Service: Decodable {
        let id: String
        let name: String
        let url: URL
        let status: Status?
        let lastCheck: Date?

        enum Status: String, Decodable {
            case ok = "OK"
            case fail = "FAIL"
        }
    }
}

extension Ping {

    struct AddRequest: Encodable {
        let url: URL
        let name: String
    }
}
