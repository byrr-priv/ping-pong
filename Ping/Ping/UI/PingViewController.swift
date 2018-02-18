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
        tableView.tableFooterView = UIView()

        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshControlPulled), for: .primaryActionTriggered)
        tableView.refreshControl = refreshControl
    }

    private func listenToViewModelChanges() {
        model.services.bind { [weak self] (services) in
            self?.tableView.reloadData()
            self?.tableView.refreshControl?.endRefreshing()
        }.unbind(with: group)
    }

    @objc private func refreshControlPulled() {
        model.fetchServices()
    }

    @IBAction func addButtonPressed(_ sender: Any) {
        presentAddNewPopup()
    }

    private func presentAddNewPopup() {
        let alert = UIAlertController(title: "Add new", message: nil, preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "Name"
        }
        alert.addTextField { (textField) in
            textField.placeholder = "https://www.google.com/"
        }
        alert.addAction(UIAlertAction(title: "Done", style: .default, handler: { action in
            let name = alert.textFields![0].text ?? ""
            let url = alert.textFields![1].text ?? ""
            self.addService(name: name, url: url)
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        present(alert, animated: true, completion: nil)
    }

    private func addService(name: String, url: String) {
        do {
            try model.addService(name: name, url: url)
        } catch PingViewModel.InputError.nameEmpty {
            alert(errorText: "Name needs to be non-empty")
        } catch PingViewModel.InputError.malformedURL {
            alert(errorText: "Malformed URL")
        } catch {}
    }

    private func alert(errorText: String) {
        let alert = UIAlertController(title: "Error", message: errorText, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Close", style: .cancel, handler: nil))
        present(alert, animated: true, completion: nil)
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

    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }

    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCellEditingStyle {
        return .delete
    }

    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        model.deleteService(at: indexPath.row)
    }
}

extension PingViewController: UITableViewDelegate {

}
