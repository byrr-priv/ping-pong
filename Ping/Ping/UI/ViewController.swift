//
//  Copyright © 2018 Frallware. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        let api = Services.find(type: PingAPI.self)
        api.getAll()
            .onSuccess { (res) in
                print(res)
            }
            .onError(handler: { (err) in
                print(err)
            })
            .start()
    }
}
