//
//  Copyright © 2018 Frallware. All rights reserved.
//

import UIKit

private let dateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "yyyy-MM-dd HH:mm"
    return formatter
}()

class PingCell: UITableViewCell, NibBased {

    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var urlLabel: UILabel!
    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!

    func configure(with service: Ping.Service) -> PingCell {
        nameLabel.text = service.name
        urlLabel.text = service.url.absoluteString
        timeLabel.text = service.lastCheck.map(dateFormatter.string)

        switch service.status {
        case .ok?:
            statusLabel.text = "OK"
            statusLabel.textColor = UIColor.green.withAlphaComponent(0.7)
        case .fail?:
            statusLabel.text = "FAIL"
            statusLabel.textColor = UIColor.red.withAlphaComponent(0.7)
        case nil:
            statusLabel.text = "Pending..."
            statusLabel.textColor = UIColor.yellow.withAlphaComponent(0.7)
        }

        return self
    }
}
