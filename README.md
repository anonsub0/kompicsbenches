Omni-Paxos Experiments
===========================================
# Experimental Setup/Requirements
- Ubuntu 18.04
- Rust: rustc 1.53.0-nightly (42816d61e 2021-04-24)
- JDK: openjdk 11.0.11 2021-04-20
- SBT
- Ammonite
- protobuf 3.6 (in particular protoc)

(See next section for installing all of these)

 We used Google Cloud Compute `e2-standard-8` instances. We require one client instance and between 3-8 server instances. In Running we will provide the number of instances required per experiment.

 Installation Guide and Building must be performed on all VM instances. For convenience, it is recommended to do all steps for the client instancce first and then create a machine image from it that is used to deploy the server instances. Furthermore, we recommend using the internal IP addresses when needed in the following steps.

# Installation Guide

1. Clone this repo and run installation script: `./install_dependencies.sh`

On client instance:

2. Create ssh keys (without passcode): `ssh-keygen` 

On all server instances:

3. Add the public key created in step 3 to `~/.ssh/authorized_keys`

On client instance:

4. `ssh` to all servers to authenticate and make sure client can reach servers.
5. Add all client ip addresses to the file `nodes.conf`. Also add all ip addresses to the file `kompact/configs/atomic_broadcast.conf` under "deployment"
6. Set `runnerAddr`  to `<client_ip>:45678` and `masterAddr` to `<client_ip>:45679` [here](https://github.com/anonsub0/kompicsbenches/blob/main/bench.sc#L18-L20). Furthermore set ssh key [here](https://github.com/anonsub0/kompicsbenches/blob/main/bench.sc#L326).

# Building
On all instances:

`./build.sc --useOnly shared_scala; ./build.sc --useOnly runner; ./build.sc --useOnly kompact`

# Running
On client instance (after built on all instances):
`./bench.sc remote --impls KOMPACTMIX --benchmarks ATOMICBROADCAST --runName <branchName>`

This runs the **General Performance and Partial Connectivity experiments.**

## Reconfiguration experiments
On client and server instances:
1. Switch branch to `reconfig`.
(Optional: To collect IO metadata run `./build.sc --useOnly kompact;` on all server instances)

On client instance:

2. Rebuild runner: `./build.sc --useOnly runner;`
3. Run experiment (see "Running")

## Leader Election Experiments
On client and server instances:
1. Switch branch to `leader`.

On client instance:

2. Rebuild runner: `./build.sc --useOnly runner;`
3. Make sure to update `nodes.conf` with new ip addresses and put the straggler server i.e. with weaker cpu or worse location as `1` in `kompact/configs/atomic_broadcast.conf`
4. Run experiment (see "Running")

# Collecting Results
(Optional) Collect all server logs and meta results into one zip file: \
 &nbsp;&nbsp;&nbsp;0. ssh into all server instances and run: `./zip_logs_results.sh <runName>`

On client instance:
1. `mkdir meta_results/<runName>/remote `
2. run for each server instance: `scp <instance_name>@<instance_ip>:~/kompicsbenches/zipped/<runName>.zip`
3. `./zip_logs_results.sh`

All results, meta results and logs will be at: `zipped/<runName>.zip`

# Plotting
See [Plotting.md](https://github.com/anonsub0/kompicsbenches/blob/main/Plotting.md).