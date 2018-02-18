//
//  Copyright © 2018 Frallware. All rights reserved.
//

import Foundation
import API

enum PingAPIResult<T> {
    case success(T)
    case error(Error)
}

protocol PingAPI {

    typealias Callback<T> = (PingAPIResult<T>) -> Void

    func getAll() -> HTTPClient.Task<Ping.Response>
    func add(_ request: Ping.AddRequest) -> HTTPClient.Task<Void>
    func delete(id: String) -> HTTPClient.Task<Void>
}

class _PingAPI: PingAPI {

    private let client: HTTPClient

    private let encoder = JSONEncoder()

    private let decoder: JSONDecoder = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm"

        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .formatted(formatter)
        return decoder
    }()


    init(baseURL: URL) {
        self.client = HTTPClient(baseURL: baseURL)
    }

    func getAll() -> HTTPClient.Task<Ping.Response> {
        return client
            .dataRequest(.get, path: "/service", body: nil)
            .map(to: Ping.Response.self, with: decoder)
    }

    func add(_ request: Ping.AddRequest) -> HTTPClient.Task<Void> {
        let data = try! encoder.encode(request)
        return client
            .completingRequest(.post, path: "/service", body: data)
    }

    func delete(id: String) -> HTTPClient.Task<Void> {
        return client
            .completingRequest(.delete, path: "/service/\(id)", body: nil)
    }
}
