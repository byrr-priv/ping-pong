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
        statusLabel.text = service.status?.rawValue ?? "PENDING"
        timeLabel.text = service.lastCheck.map(dateFormatter.string)
        return self
    }
}
