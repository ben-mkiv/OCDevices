require("nanoAnalyzerPing")

term.clear()
print("")

commands = { "getAmmo", "getEnergy", "getDurability", "getName" }

doStuff = function() end

stopMe = false

function getData(evt, dst, src, port, distance, ...)
    local data = { ... }
    --term.clear()
    print("data from: " .. src)
    for i=1,#data do
        print(data[i])
    end
    print("")
end

function stopAll()
    stopMe = true
end

host = nil
command = nil

io.write("pinging NanoAnalyzers... ")
hosts = pingAll()
io.write("[DONE]\n\n")


function selectHost()
    print("# Hosts")
    for i=1,#hosts do
        print("["..i.."] (" .. hosts[i][1] .. ")\t" .. hosts[i][2])
    end

    gpu.setForeground(0xFFFF00)
    io.write("\nselect host or press enter to exit: ")
    gpu.setForeground(0xFFFFFF)
    local userInput = io.read()

    if #userInput == 0 then
        stopAll()
    elseif hosts[tonumber(userInput)] then
        host = hosts[tonumber(userInput)]
    else
        return false
    end

    return true
end

function selectCommand()
    local hosts = pingAll()
    print("# Commands")
    for i=1,#commands do
        print("["..i.."] " .. commands[i])
    end

    gpu.setForeground(0xFFFF00)
    io.write("\nselect command or press enter to exit: ")
    gpu.setForeground(0xFFFFFF)
    local userInput = io.read()

    if #userInput == 0 then
        stopAll()
    elseif commands[tonumber(userInput)] then
        command = commands[tonumber(userInput)]
    else
        return false
    end

    return true
end

doStuff = function()
    width, height = gpu.getResolution()
    x, y = term.getCursor()

    gpu.setForeground(0xFF00FF)
    gpu.fill(1, y, width, 1, "#")
    gpu.setForeground(0xFFFFFF)

    print("")

    host = nil
    command = nil

    while not selectHost() do
        os.sleep(0.05)
    end

    if host ~= nil then
        while not selectCommand() do
            os.sleep(0.05)
        end

        if command ~= nil then
            modem.send(host[1], port, command)
            os.sleep(0.5)
        end
    end

    if stopMe == false then
        doStuff()
    end
end

event.listen("interrupted", stopAll)
event.listen("modem_message", getData)
doStuff()
while not stopMe do os.sleep(0.2) end
event.ignore("modem_message", getData)
event.ignore("interrupted", stopAll)
