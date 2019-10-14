component = require("component")
modem = component.modem
gpu = component.gpu
event = require("event")
term = require("term")
port = 333

timeout = 20
replies = {}

function processPingReply(evt, dst, src, port, distance, cmd, name)
    --print(src .. "\t" .. name)
    table.insert(replies, { src, name })
    timeout = 20
end

function pingAll()
    modem.open(port)
    --term.clear()
    --print("")
    --print("")
    event.listen("modem_message", processPingReply)

    modem.broadcast(port, "ping")

    ticks = 1

    while timeout > 0 do
        --gpu.set(ticks, 1, ".")
        os.sleep(0.05)
        timeout = timeout - 1
        ticks = ticks + 1
    end

    event.ignore("modem_message", processPingReply)

    return replies
end