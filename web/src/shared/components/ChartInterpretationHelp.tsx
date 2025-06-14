import { Collapse, Space } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';

interface ChartInterpretationHelpProps {
  message: string;
}

const ChartInterpretationHelp = (props: ChartInterpretationHelpProps) => {
  const { message } = props;
  return (
    <Collapse
      bordered={false}
      ghost={true}
      expandIconPosition="end"
      items={[
        {
          key: '1',
          label: (
            <Space>
              <QuestionCircleOutlined />
              <span>How should you interpret this chart?</span>
            </Space>
          ),
          children: message,
        },
      ]}
    />
  );
};

export default ChartInterpretationHelp;
