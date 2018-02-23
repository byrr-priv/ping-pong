//
//  Copyright © 2018 Frallware. All rights reserved.
//

import Foundation
import Bindings

final class PingViewModel {

    enum InputError: Error {
        case nameEmpty
        case malformedURL
    }

    let services = Variable<[Ping.Service]>([])

}

extension PingViewModel {

    func fetchServices() {
        let api = Services.find(type: PingAPI.self)
        api.getAll()
            .completing(on: .main)
            .onSuccess { (res) in
                self.services.set(res.services)
            }
            .onError { (error) in
                debugPrint(error)
            }
            .start()
    }

    func addService(name: String, url string: String) throws {
        guard !name.isEmpty else {
            throw InputError.nameEmpty
        }
        guard let url = URL(string: string) else {
            throw InputError.malformedURL
        }

        let request = Ping.AddRequest(url: url, name: name)
        let api = Services.find(type: PingAPI.self)
        api.add(request)
            .completing(on: .main)
            .onSuccess {
                self.fetchServices()
            }
            .onError { (error) in
                debugPrint(error)
            }
            .start()
    }

    func deleteService(at index: Int) {
        let service = services.value[index]
        let api = Services.find(type: PingAPI.self)
        api.delete(id: service.id)
            .completing(on: .main)
            .onSuccess {
                self.fetchServices()
            }
            .onError { (error) in
                debugPrint(error)
            }
            .start()
    }
}
