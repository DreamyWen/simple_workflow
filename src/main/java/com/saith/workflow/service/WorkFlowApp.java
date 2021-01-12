package com.saith.workflow.service;

public class WorkFlowApp {

    public static void main(String[] args) {
//>>> func_a = Operator(name='A')
//                >>> func_b = Operator(name='B')
//                >>> data = Data(url='hdfs://abc.txt')
//                >>> data_a = func_a(data)
//                >>> data_b = func_b(data_a)
//                >>> print(data_b.to_workflow())
//... {
//...    'nodes': [{'name': 'hdfs://abc.txt'， 'type': 'data', 'id': '1'},
//            {'name': 'A'， 'type': 'operator', 'id': '2'},
//            {'name': 'B'， 'type': 'operator', 'id': '3'}],
//...    'edges': [{'src_node': '1', 'dest_node': 2}, {'src_node': '2', 'dest_node': 3}]
//... }


        String out = Pipeline.applyBegin(Data.of("url='hdfs://abc.txt'"))
                .apply(new PrintFunction("A"))
                .apply(new PrintFunction("B"))
//                .apply(new PrintFunction("C"))
                .toWorkFlow();
        System.out.println(out);


    }
}
