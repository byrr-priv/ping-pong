//
//  Copyright © 2018 Frallware. All rights reserved.
//

import UIKit
import Bindings

class PingViewController: UIViewController {

    private let model = PingViewModel()
    private let group = BindingGroup()

    @IBOutlet weak var tableView: UITableView!

    override func viewDidLoad() {
        super.viewDidLoad()
        setupTableView()
        listenToViewModelChanges()
        model.fetchServices()
    }

    private func setupTableView() {
        PingCell.registerAsNib(in: tableView)

        tableView.dataSource = self
        tableView.delegate = self
        tableView.allowsSelection = false
    }

    private func listenToViewModelChanges() {
        model.services.bind { [weak self] (services) in
            self?.tableView.reloadData()
        }.unbind(with: group)
    }
}

extension PingViewController: UITableViewDataSource {

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return model.services.value.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView
            .dequeueCell(type: PingCell.self, at: indexPath)
            .configure(with: model.services.value[indexPath.row])
    }
}

extension PingViewController: UITableViewDelegate {

}
