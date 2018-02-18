//
//  Copyright © 2018 Frallware. All rights reserved.
//

import Foundation

public struct HTTPMethod {
    let stringValue: String

    init(_ stringValue: String) {
        self.stringValue = stringValue
    }
}

public extension HTTPMethod {
    public static let get = HTTPMethod("GET")
    public static let post = HTTPMethod("POST")
}

public class HTTPClient {

    private static let urlSession = URLSession(configuration: .default)

    private let baseURL: URL
    private let decoder: JSONDecoder

    init(baseURL: URL, decoder: JSONDecoder = JSONDecoder()) {
        self.baseURL = baseURL
        self.decoder = decoder
    }

    func perform<T>(_ method: HTTPMethod, path: String, responseType type: T.Type) -> Task<T> {
        guard let url = URL(string: baseURL.absoluteString.appending(path)) else {
            fatalError()
        }

        return self.perform(method, url: url, responseType: type)
    }

    func perform<T>(_ method: HTTPMethod, url: URL, responseType type: T.Type) -> Task<T> {

        var request = URLRequest(url: url)
        request.httpMethod = method.stringValue

        let task = Task<T>()
        task.task = HTTPClient.urlSession.dataTask(with: request) { (data, response, error) in
            if let error = error {
                task.consume(error: error)
            } else if let data = data {
                task.consume(data: data)
            }
        }
        task.decoder = decoder
        return task
    }
}

public extension HTTPClient {

    public class Task<T: Decodable> {

        public enum DebugPrintMode {
            case none, raw, parsed
        }

        fileprivate var task: URLSessionTask!
        fileprivate var decoder: JSONDecoder!

        private var callbackQueue: DispatchQueue?
        private var successHandler: ((T) -> Void)?
        private var errorHandler: ((Error) -> Void)?
        private var debugPrintMode: DebugPrintMode = .none

        @discardableResult
        public func start() -> Task<T> {
            task.resume()
            return self
        }

        @discardableResult
        public func cancel() -> Task<T> {
            task.cancel()
            return self
        }

        public func queue(_ queue: DispatchQueue) -> Task<T> {
            callbackQueue = queue
            return self
        }

        public func debugPrint(_ mode: DebugPrintMode) -> Task<T> {
            debugPrintMode = mode
            return self
        }

        public func onSuccess(handler: @escaping (T) -> Void) -> Task<T> {
            successHandler = handler
            return self
        }

        public func onError(handler: @escaping (Error) -> Void) -> Task<T> {
            errorHandler = handler
            return self
        }

        fileprivate func consume(data: Data) {
            do {
                if debugPrintMode == .raw, let string = String(data: data, encoding: .utf8) {
                    print(string)
                }
                let response = try decoder.decode(T.self, from: data)
                if debugPrintMode == .parsed {
                    print(response)
                }

                if let queue = callbackQueue {
                    queue.async {
                        self.successHandler?(response)
                    }
                } else {
                    successHandler?(response)
                }
            } catch let error {
                consume(error: error)
            }
        }

        fileprivate func consume(error: Error) {
            if let queue = callbackQueue {
                queue.async {
                    self.errorHandler?(error)
                }
            } else {
                errorHandler?(error)
            }
        }
    }
}
