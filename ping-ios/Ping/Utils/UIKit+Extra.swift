//
//  Copyright © 2018 Frallware. All rights reserved.
//

import UIKit

protocol NibBased {
    static var nib: UINib { get }
}

extension NibBased {
    static var nib: UINib {
        return UINib(nibName: String(describing: self), bundle: nil)
    }
}

extension NibBased where Self: UITableViewCell {

    static func registerAsNib(in tableView: UITableView) {
        tableView.register(nib, forCellReuseIdentifier: defaultReuseIdentifier)
    }
}

extension UITableViewCell {

    static var defaultReuseIdentifier: String {
        return String(describing: self)
    }
}

extension UITableView {

    func dequeueCell<C: UITableViewCell>(type: C.Type, at indexPath: IndexPath) -> C {
        return dequeueReusableCell(withIdentifier: type.defaultReuseIdentifier, for: indexPath) as! C
    }
}
