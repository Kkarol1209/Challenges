using (var conn = EventStoreConnection.Create(new Uri(“tcp://localhost:1113”)))
{
    conn.ConnectAsync().Wait();
}
var NewRequest = new NewRequestCreated(New Request()
{
    ClientName = “Karoline”,
    Amount = 1600
});//Created event, Ecommerce.Pedidos will concentrate all events related to orders placed in our store.


conn.AppendToStreamAsync(
    “Ecommerce.Orders”,
    ExpectedVersion.Any,
    GenerateEvent(NewRequest)).Wait();

private static EventData GenerateEvent(Event event)
{
    return new EventData(
        event.Id,
        event.GetType().Name,
        true,
        Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(event)), null);
}//Event posted, accessing the Event Store administrative console to view the event created.

var lastProcessedEvent = 0;

using (var conn = EventStoreConnection.Create(new Uri(“tcp://localhost:1113”)))
{
    conn.ConnectAsync().Wait();

while ((Console.ReadLine() != null))
    {

        var itens = conn.ReadStreamEventsForwardAsync(
“Ecommerce.Pedidos”, 
lastProcessedEvent + 1, 200, false).Result;

if (itens.Events.Any())
        {
            foreach (var e in itens.Events)
                Process(conn, e);
        }
        else
        {
            Log.Yellow(“There are no events to process.”);
        }
    }
}//Reads events in the order they were posted.

internal static void Process(
IEventStoreConnection connection, 
ResolvedEvent eventData)
{
    if (eventData.Event.EventType == “NewOrderCreated”)
    {
        var newOrder = Extrair<NewOrderCreated>(eventData);

Log.Green(“New Order”);
        Log.Green(“Id: ” + newOrder.Id, 4);
        Log.Green(“Data: ” + newOrder.Data, 4);
        Log.Green(“Client: ” + newOrder.NomeDoCliente, 4);
        Log.Green(“Value: ” + newOrder.ValorTotal.ToString(“N2”), 4);
        Log.Yellow(“Issuing the Order Invoice”, 4);
        Log.NewLine();

connection.AppendToStreamAsync(
            “Ecommerce.requests”,
            ExpectedVersion.Any,
generateEvent(
new Invoice Issued(
newOrder.CustomerName,
newOrder.TotalValue,
“0001.0292.2999-2881-9918.11.9999/99”))).Wait();

lastProcessedEvent = eventData.Event.EventNumber;
    }
}

private static TEvento Extract<TEvento>(ResolvedEvent eventData)
where TEvento : Event
{
return JsonConvert.DeserializeObject<TEvent>(
Encoding.UTF8.GetString(eventData.Event.Data));
}//the Process method identifies that it is an event of this type, deserializes it, processes it and after that, triggers an event saying that the respective invoice has been issued, through a new event.

using (var conn = EventStoreConnection.Create(new Uri("tcp://localhost:1113")))
{
    conn.ConnectAsync().Wait();

    conn.SubscribeToStreamAsync(
“Ecommerce.Orders”,
false,
(a, e) => Process(e)).Wait();

Console.ReadLine();
}

internal static void Process(ResolvedEvent eventData)
{
    if (eventData.Event.EventType == "NotaFiscalIssued")
    {
        var newOrder = Extract<NotaFiscalIssued>(eventData);

Log.Green("NEW ORDER - INVOICE ISSUED");
        Log.Green(“Id: ” + newOrder.Id, 4);
        Log.Green(“Customer: ” + newOrder.CustomerName, 4);
        Log.Green(“Value: ” + newOrder.TotalValue.ToString(“N2”), 4);
        Log.Green(“NF-e: ” + newOrder.KeyDaNotaFiscalEletronica, 4);
        Log.NewLine();
    }
}//And now the screens, one generates the order, the second issues the invoice and the third is notified to start the transport process.