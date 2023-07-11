import {
  ExclamationCircleOutlined,
  PieChartOutlined,
  RiseOutlined,
  WalletOutlined,
  DollarCircleOutlined,
} from '@ant-design/icons';
import { Card, Col, Divider, Modal, Row, Slider, Space, Statistic } from 'antd';
import { useState } from 'react';
import { Land, Player } from '../redux';
import axios from 'axios';

export interface InvestModalProps {
  open?: boolean;
  player?: Player;
  land?: Land;
  investText?: string;
  onCancel?: () => void;
}

export const InvestModal = (props: InvestModalProps) => {
  const { open, player, land, investText, onCancel } = props;
  const [ownership, setOwnership] = useState(0);
  if (!open || !player || !land || !investText || !onCancel) {
    return null;
  }

  // calculating investment-related parameters
  const userInvestAmount = (ownership * land.marketValue) / 100;
  const maxAvailOwnership = 100 - land.totalOwnership;
  const maxOfferOwnership = Math.min(
    maxAvailOwnership,
    Math.floor((player.cash * 100) / land.marketValue)
  );

  const invest = async () => {
    await axios.post(`/api/players/${player.id}/lands`, {
      landId: land.id,
      ownership,
    });
    setOwnership(0);
    onCancel();

    // Ending player's turn after investing in any land
    axios.put('/api/game/next');
  };

  return (
    <Modal
      className="strategists-actions__modal"
      title={
        <div className="strategists-actions__modal__title">
          {investText}
          <Space>
            <small>
              <WalletOutlined /> {player.cash} cash available
            </small>
            <Divider type="vertical" />
            <small>
              <PieChartOutlined /> {maxAvailOwnership}% shares available
            </small>
          </Space>
        </div>
      }
      open={!!open}
      okText={
        <>
          <RiseOutlined /> Invest
        </>
      }
      onOk={invest}
      onCancel={onCancel}
      okButtonProps={{
        disabled: userInvestAmount > player.cash,
      }}
    >
      <Divider />
      <main className="strategists-actions__modal__body">
        <Row>
          <Col span={12}>
            <Card bordered={false}>
              <Statistic
                title="Proposed Ownership"
                value={ownership}
                precision={0}
                prefix={<RiseOutlined />}
                suffix="%"
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card bordered={false}>
              <Statistic
                title="Cost of Investment"
                value={userInvestAmount}
                precision={2}
                prefix={<DollarCircleOutlined />}
              />
            </Card>
          </Col>
        </Row>
        <Slider
          defaultValue={ownership}
          min={0}
          max={maxOfferOwnership}
          onAfterChange={(value) => setOwnership(value)}
          tooltip={{
            formatter: (value) => `${value}%`,
          }}
        />
        {
          // This will show a warning to user that their current balance is less then the maximum available ownership.
          maxOfferOwnership !== maxAvailOwnership ? (
            <>
              <ExclamationCircleOutlined />
              Investment capped at {maxOfferOwnership}% due to low cash!
            </>
          ) : null
        }
      </main>
      <Divider />
    </Modal>
  );
};
