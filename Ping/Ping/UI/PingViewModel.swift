//
//  Copyright © 2018 Frallware. All rights reserved.
//

import Foundation
import Bindings

final class PingViewModel {

    let services = Variable<[Ping.Service]>([])

}

extension PingViewModel {

    func fetchServices() {
        let api = Services.find(type: PingAPI.self)
        api.getAll()
            .dispatch(on: .main)
            .onSuccess { (res) in
                self.services.set(res.services)
            }
            .onError(handler: { (err) in
                debugPrint(err)
            })
            .start()
    }
}
